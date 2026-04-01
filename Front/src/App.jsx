
import { Routes, Route } from "react-router-dom";
import Login from "./components/login";
import Header from "./components/header";
import { useState } from 'react'
import Demo from './components/demo'
import './App.css'

const views = {
  login: 'login',
  demo: 'demo',
}

function App() {
  return (
    <>
      <Header />

      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/testes" element={<Demo />} />
      </Routes>
    </>
  );
}

export default App;