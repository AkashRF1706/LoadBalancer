// index.js
import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import App from './App';
import Dashboard from './components/Dashboard';
import RegisterServer from './components/RegisterServer';
import './index.css';

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <Router>
    <Routes>
      <Route path="/" element={<App />}> 
        <Route index element={<Dashboard />} />
        <Route path="register" element={<RegisterServer />} />
      </Route>
    </Routes>
  </Router>
);
