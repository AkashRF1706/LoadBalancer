import React, { useEffect, useState } from 'react';
import axios from 'axios';
import ServerCard from './ServerCard';
import './Dashboard.css';
import { Link } from 'react-router-dom';

const Dashboard = () => {
  const [servers, setServers] = useState([]);
  const [redirectedUrl, setRedirectedUrl] = useState('');
  const [clientId, setClientId] = useState('');
  const [selectedStrategy, setSelectedStrategy] = useState('roundrobin');

  useEffect(() => {
    let existingClientId = localStorage.getItem('clientId');
    if (!existingClientId) {
      existingClientId = `client-${Math.random().toString(36).substring(2, 10)}`;
      localStorage.setItem('clientId', existingClientId);
    }
    setClientId(existingClientId);
  }, []);

  const fetchServers = async () => {
    try {
      const response = await axios.get('/servers');
      const alive = response.data.filter(s => s.isAlive);
      const dead = response.data.filter(s => !s.isAlive);
      alive.sort((a, b) => a.url.localeCompare(b.url));
      dead.sort((a, b) => a.url.localeCompare(b.url));
      setServers([...alive, ...dead]);
    } catch (error) {
      console.error('Error fetching servers:', error);
    }
  };

  const handleRouteClick = async () => {
    try {
      const response = await axios.get(`/redirect`, {
        params: {
          clientId: clientId,
          strategy: selectedStrategy
        }
      });
      setRedirectedUrl(response.data);
    } catch (error) {
      console.error('Error during load balancing:', error);
      setRedirectedUrl('Error: Unable to redirect');
    }
  };

  useEffect(() => {
    fetchServers();
    const interval = setInterval(fetchServers, 5000);
    return () => clearInterval(interval);
  }, []);

  return (
    <div className="dashboard-container">
      <div className="sidebar">
        <h2 className="sidebar-title">LoadBalancer</h2>
        <nav className="nav-links">
          <Link to="/" className={({ isActive }) => isActive ? 'active-link' : ''}>Dashboard</Link>
          <Link to="/register" className={({ isActive }) => isActive ? 'active-link' : ''}>Server Registration</Link>
        </nav>
      </div>

      <div className="main-content">
        <header>
          <h1>Load Balancer Dashboard</h1>
        </header>

        <section className="form-section">
          <div className="form-group">
            <label htmlFor="clientId">Client ID</label>
            <input
              type="text"
              id="clientId"
              value={clientId}
              onChange={(e) => setClientId(e.target.value)}
              placeholder="Enter Client ID"
            />
          </div>

          <div className="strategy-group horizontal">
            <span>Select Strategy:</span>
            <label><input type="radio" value="roundrobin" checked={selectedStrategy === 'roundrobin'} onChange={(e) => setSelectedStrategy(e.target.value)} /> Round Robin</label>
            <label><input type="radio" value="weighted" checked={selectedStrategy === 'weighted'} onChange={(e) => setSelectedStrategy(e.target.value)} /> Weighted</label>
            <label><input type="radio" value="sticky" checked={selectedStrategy === 'sticky'} onChange={(e) => setSelectedStrategy(e.target.value)} /> Sticky</label>
          </div>

          <button className="route-button" onClick={handleRouteClick}>Route</button>

          {redirectedUrl && (
            <div className="redirect-result">
              <strong>Redirected To:</strong> {redirectedUrl}
            </div>
          )}
        </section>

        <section className="server-health">
          <h2>Server Health</h2>
          <div className="server-grid">
            {servers.map((server) => (
              <ServerCard key={server.url} server={server} />
            ))}
          </div>
        </section>
      </div>
    </div>
  );
};

export default Dashboard;