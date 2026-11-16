import { BrowserRouter, Routes, Route } from "react-router-dom";
import LandingPage from "./pages/LandingPage";
import TriagePage from "./pages/TriagePage";

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<LandingPage />} />
        <Route path="/assist" element={<TriagePage />} />
      </Routes>
    </BrowserRouter>
  );
}