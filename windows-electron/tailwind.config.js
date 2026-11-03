/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        vendeDark: "#0B132B",
        vendeCard: "#131D31",
        vendeBorder: "#1E293B",
        vendePrimary: "#2563EB",
        vendeSecondary: "#06B6D4",
        vendeSuccess: "#10B981",
        vendeWarning: "#F59E0B",
        vendeDanger: "#EF4444",
      },
    },
  },
  plugins: [],
}
