import React from 'react';
import './ServerCard.css';

const ServerCard = ({ server }) => {
  return (
    <div className={`server-card ${server.isAlive ? 'alive' : 'dead'}`}>
      <h3>{server.url}</h3>
      <p>Status: <strong>{server.isAlive ? 'Alive 🟢' : 'Dead 🔴'}</strong></p>
      <p>Weight: {server.weight}</p>
    </div>
  );
};

export default ServerCard;