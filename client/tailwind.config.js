/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{js,jsx,ts,tsx}",
  ],
  theme: {
    extend: {
      fontFamily: {
        sans: ['Roboto', 'sans-serif'],
      },
      colors: {
        'spotify-green': '#1DB954',
        'spotify-green-hover': '#1ed760',
        'spotify-green-active': '#169c46',
        'spotify-black': '#000000',
        'spotify-dark-bg': '#121212',
        'spotify-dark-card': '#181818',
        'spotify-dark-elevated': '#282828',
        'spotify-white': '#FFFFFF',
        'spotify-gray': '#B3B3B3',
        'spotify-gray-dark': '#535353',
        'spotify-error': '#E22134',
        'spotify-warning': '#FFA42B',
      },
    },
  },
  plugins: [],
}
