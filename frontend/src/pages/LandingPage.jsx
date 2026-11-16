import { useNavigate } from "react-router-dom";
import { Activity, MapPin, ShieldAlert, ArrowRight } from "lucide-react";

export default function LandingPage() {
  const navigate = useNavigate();

  return (
    <div className="landing-container">
      <header className="navbar">
        <div className="brand">
          <Activity size={26} color="#dc2626" />
          <span>MediPulse Assist</span>
        </div>
      </header>

      <main className="hero-section">
        <div className="badge">AI First-Aid Triage & Live Hospital Locator</div>
        <h1>Immediate Medical Guidance & Nearest Hospital Care</h1>
        <p className="hero-subtitle">
          Describe your symptoms or emergency condition in plain language. Our AI generates an
          instant, step-by-step immediate action checklist while pinpointing the closest hospitals
          on an interactive map from your live GPS location.
        </p>

        <button className="primary-cta" onClick={() => navigate("/assist")}>
          Start Emergency Triage Chat <ArrowRight size={20} />
        </button>

        <div className="features-grid">
          <div className="feature-card">
            <ShieldAlert size={28} color="#dc2626" />
            <h3>1. Instant First-Aid Steps</h3>
            <p>
              Get calm, structured stabilization steps and urgency assessment tailored to your
              described symptoms.
            </p>
          </div>

          <div className="feature-card">
            <MapPin size={28} color="#2563eb" />
            <h3>2. Live Hospital Map</h3>
            <p>
              Automatically detects your location and plots the nearest hospitals with distance and
              one-click navigation.
            </p>
          </div>
        </div>
      </main>
    </div>
  );
}