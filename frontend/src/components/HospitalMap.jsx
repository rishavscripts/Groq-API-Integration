import { useEffect } from "react";
import { MapContainer, TileLayer, Marker, Popup, useMap } from "react-leaflet";
import L from "leaflet";

// Custom blue pulse pin for User Location
const userIcon = L.divIcon({
  className: "custom-pin",
  html: `<div style="background:#2563eb;width:16px;height:16px;border-radius:50%;border:3px solid white;box-shadow:0 0 8px rgba(37,99,235,0.8);"></div>`,
  iconSize: [22, 22],
  iconAnchor: [11, 11],
});

// Custom red cross pin for Hospitals
const hospitalIcon = L.divIcon({
  className: "custom-pin",
  html: `<div style="background:#dc2626;color:white;font-weight:bold;font-size:14px;width:26px;height:26px;border-radius:50%;display:flex;align-items:center;justify-content:center;border:2px solid white;box-shadow:0 2px 6px rgba(0,0,0,0.35);">+</div>`,
  iconSize: [26, 26],
  iconAnchor: [13, 13],
});

// Helper to re-center map smoothly when user coordinates update
function RecenterMap({ center }) {
  const map = useMap();
  useEffect(() => {
    if (center) {
      map.flyTo(center, 14, { duration: 1.2 });
    }
  }, [center, map]);
  return null;
}

export default function HospitalMap({ userLocation, hospitals, selectedHospital }) {
  // Default center (Kolkata) before GPS resolves
  const center = userLocation ? [userLocation.lat, userLocation.lon] : [22.6956, 88.3746];

  return (
    <MapContainer center={center} zoom={13} style={{ height: "100%", width: "100%", borderRadius: "12px" }}>
      <TileLayer
        attribution='© <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
      />

      <RecenterMap
        center={
          selectedHospital
            ? [selectedHospital.latitude, selectedHospital.longitude]
            : userLocation
            ? [userLocation.lat, userLocation.lon]
            : null
        }
      />

      {/* User Marker */}
      {userLocation && (
        <Marker position={[userLocation.lat, userLocation.lon]} icon={userIcon}>
          <Popup>
            <strong>You are here</strong>
          </Popup>
        </Marker>
      )}

      {/* Hospital Markers */}
      {hospitals.map((hospital) => (
        <Marker
          key={hospital.id}
          position={[hospital.latitude, hospital.longitude]}
          icon={hospitalIcon}
        >
          <Popup>
            <div style={{ minWidth: "160px" }}>
              <strong>{hospital.name}</strong>
              <p style={{ margin: "4px 0", fontSize: "12px" }}>{hospital.distanceKm} km away</p>
              <a
                href={`https://www.google.com/maps/dir/?api=1&destination=${hospital.latitude},${hospital.longitude}`}
                target="_blank"
                rel="noreferrer"
                style={{ color: "#2563eb", fontWeight: "600", fontSize: "12px" }}
              >
                Open Directions →
              </a>
            </div>
          </Popup>
        </Marker>
      ))}
    </MapContainer>
  );
}