/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        primary: {
          50: '#f5f7ff',
          100: '#ebedff',
          200: '#dce0ff',
          300: '#c2c9ff',
          400: '#9fa8ff',
          500: '#757cff',
          600: '#5356ff',
          700: '#4140eb',
          800: '#3432be',
          900: '#2c2c97',
        },
      },
    },
  },
  plugins: [],
}
