
import { Routes, Route, useLocation } from "react-router-dom";
import Login from "./components/login";
import Header from "./components/header";
import Demo from './components/demo'
import './App.css'

function App() {
  const location = useLocation()
  const hideHeader = location.pathname === '/testes'

  return (
    <>
      {!hideHeader && <Header />}

      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/testes" element={<Demo />} />
      </Routes>
    </>
  );
}

export default App;