import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { ArrowLeft, Send, Navigation, AlertTriangle, Hospital } from "lucide-react";
import HospitalMap from "../components/HospitalMap";

export default function TriagePage() {
  const navigate = useNavigate();
  const [query, setQuery] = useState("");
  const [userLocation, setUserLocation] = useState(null);
  const [locationError, setLocationError] = useState("");
  const [loading, setLoading] = useState(false);
  const [response, setResponse] = useState(null);
  const [selectedHospital, setSelectedHospital] = useState(null);

  // Grab user GPS on page load so the map is immediately centered on them
  useEffect(() => {
    if (!navigator.geolocation) {
      setLocationError("Geolocation is not supported by your browser.");
      return;
    }

    navigator.geolocation.getCurrentPosition(
      (pos) => {
        setUserLocation({
          lat: pos.coords.latitude,
          lon: pos.coords.longitude,
        });
      },
      () => {
        setLocationError("Location permission denied. Enable GPS to locate nearby hospitals.");
      }
    );
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!query.trim()) return;
    if (!userLocation) {
      alert("Please allow location access so we can find hospitals near you.");
      return;
    }

    setLoading(true);
    setSelectedHospital(null);

    try {
      const res = await fetch("http://localhost:8080/api/emergency/assist", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          condition: query,
          latitude: userLocation.lat,
          longitude: userLocation.lon,
          radiusMeters: 5000,
        }),
      });

      if (!res.ok) throw new Error("Backend request failed");
      const data = await res.json();
      setResponse(data);
    } catch (err) {
      alert("Could not connect to the triage server. Ensure Spring Boot is running on port 8080.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="triage-layout">
      {/* Top Bar */}
      <header className="triage-header">
        <button className="back-btn" onClick={() => navigate("/")}>
          <ArrowLeft size={18} /> Home
        </button>
        <h2>Emergency Triage & Hospital Finder</h2>
        <span className="gps-status">
          {userLocation ? "🟢 GPS Active" : "🔴 Waiting for GPS"}
        </span>
      </header>

      {/* Main Split Workspace */}
      <div className="workspace-grid">
        {/* Left Panel: Query Input, AI Action List & Hospital Cards */}
        <div className="left-panel">
          <form onSubmit={handleSubmit} className="query-box">
            <label htmlFor="symptom-input">Describe the medical condition or symptoms:</label>
            <div className="input-row">
              <textarea
                id="symptom-input"
                rows="3"
                placeholder="e.g., Sudden dizziness, blurred vision, and high blood pressure..."
                value={query}
                onChange={(e) => setQuery(e.target.value)}
                required
              />
              <button type="submit" disabled={loading} className="send-btn">
                {loading ? "Analyzing..." : <><Send size={18} /> Analyze</>}
              </button>
            </div>
          </form>

          {locationError && <div className="error-banner">{locationError}</div>}

          {response && (
            <div className="results-container">
              {/* AI Triage Guidance Card */}
              <div className={`guidance-card urgency-${response.guidance.urgencyLevel.toLowerCase()}`}>
                <div className="guidance-header">
                  <span className="urgency-pill">
                    <AlertTriangle size={15} /> Urgency: {response.guidance.urgencyLevel}
                  </span>
                  <span className="specialty-tag">
                    Dept: {response.guidance.recommendedSpecialty}
                  </span>
                </div>

                <h4>Immediate Actions to Take:</h4>
                <ol className="action-list">
                  {response.guidance.immediateActions.map((step, index) => (
                    <li key={index}>{step}</li>
                  ))}
                </ol>
                <p className="disclaimer">{response.guidance.disclaimer}</p>
              </div>

              {/* Nearest Hospitals List */}
              <div className="hospitals-section">
                <h4>
                  <Hospital size={18} /> Nearest Hospitals ({response.nearestHospitals.length})
                </h4>
                {response.nearestHospitals.map((hospital) => (
                  <div
                    key={hospital.id}
                    className={`hospital-item ${selectedHospital?.id === hospital.id ? "active" : ""}`}
                    onClick={() => setSelectedHospital(hospital)}
                  >
                    <div>
                      <strong>{hospital.name}</strong>
                      <span className="distance-badge">{hospital.distanceKm} km</span>
                      <p className="hospital-addr">{hospital.address}</p>
                    </div>
                    <a
                      href={`https://www.google.com/maps/dir/?api=1&destination=${hospital.latitude},${hospital.longitude}`}
                      target="_blank"
                      rel="noreferrer"
                      className="nav-link"
                      onClick={(e) => e.stopPropagation()}
                    >
                      <Navigation size={15} /> Go
                    </a>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>

        {/* Right Panel: Interactive Map */}
        <div className="right-panel">
          <HospitalMap
            userLocation={userLocation}
            hospitals={response?.nearestHospitals || []}
            selectedHospital={selectedHospital}
          />
        </div>
      </div>
    </div>
  );
}