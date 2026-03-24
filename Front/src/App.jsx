import { Routes, Route } from "react-router-dom";
import Login from "./components/login";
import Header from "./components/header";

function App() {
  return (
    <>
      <Header />

      <Routes>
        <Route path="/" element={<Login />} />
      </Routes>
    </>
  );
}

export default App;