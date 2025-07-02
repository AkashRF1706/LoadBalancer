import React, { useEffect, useState } from 'react';
import axios from 'axios';
import './RegisterServer.css';
import { Link } from 'react-router-dom';

const RegisterServer = () => {
  const [url, setUrl] = useState('');
  const [weight, setWeight] = useState(1);
  const [message, setMessage] = useState('');
  const [servers, setServers] = useState([]);

  const fetchServers = async () => {
    try {
      const response = await axios.get('/servers');
      const sorted = response.data.sort((a, b) => a.url.localeCompare(b.url));
      setServers(sorted);
    } catch (error) {
      console.error('Error fetching servers:', error);
    }
  };

  const handleRegister = async () => {
    try {
      const newServer = {
        url,
        weight,
        isAlive: true
      };
      const response = await axios.post('/servers/register', newServer);
      setMessage(response.data);
      fetchServers();
      setUrl('');
      setWeight(1);
    } catch (error) {
      setMessage(error?.response?.data || 'Error registering server');
    }
  };

  const handleRemove = async (serverUrl) => {
    try {
      const response = await axios.delete('/servers/remove', {
        data: { url: serverUrl }
      });
      setMessage(response.data);
      fetchServers();
    } catch (error) {
      setMessage(error?.response?.data || 'Error removing server');
    }
  };

  useEffect(() => {
    fetchServers();
  }, []);

  return (
    <div className="registration-container">
      <div className="sidebar">
        <h2>LoadBalancer</h2>
        <nav>
          <ul>
            <li><Link to="/">Dashboard</Link></li>
            <li><Link to="/register">Server Registration</Link></li>
          </ul>
        </nav>
      </div>

      <div className="main-content">
        <header>
          <h1>Server Registration</h1>
        </header>

        <section className="form-section">
          <div className="form-group">
            <label htmlFor="url">Server URL</label>
            <input
              type="text"
              id="url"
              value={url}
              onChange={(e) => setUrl(e.target.value)}
              placeholder="http://localhost:8081"
            />
          </div>

          <div className="form-group">
            <label htmlFor="weight">Weight</label>
            <input
              type="number"
              id="weight"
              value={weight}
              onChange={(e) => setWeight(Number(e.target.value))}
              min="1"
            />
          </div>

          <button onClick={handleRegister} className="register-button">Register Server</button>

          {message && <div className="message-box">{message}</div>}
        </section>

        <section className="server-list">
          <h2>Registered Servers</h2>
          <ul>
            {servers.map(server => (
              <li key={server.url}>
                {server.url} (Weight: {server.weight})
                <button className="remove-button" onClick={() => handleRemove(server.url)}>Remove</button>
              </li>
            ))}
          </ul>
        </section>
      </div>
    </div>
  );
};

export default RegisterServer;