import React from 'react';

/**
 * Footer component with app information and links
 */
const Footer: React.FC = () => {
  const currentYear = new Date().getFullYear();

  return (
    <footer className="app-footer">
      <div className="footer-content">
        <div className="footer-section">
          <p className="footer-text">
            &copy; {currentYear} Setlist Playlist. All rights reserved.
          </p>
        </div>
        <div className="footer-section">
          <div className="footer-links">
            <a
              href="https://www.setlist.fm"
              target="_blank"
              rel="noopener noreferrer"
              className="footer-link"
            >
              Powered by Setlist.fm
            </a>
            <span className="footer-divider">|</span>
            <a
              href="https://www.spotify.com"
              target="_blank"
              rel="noopener noreferrer"
              className="footer-link"
            >
              Spotify
            </a>
          </div>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
