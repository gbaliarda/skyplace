/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./pages/**/*.{js,ts,jsx,tsx}",
    "./components/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      animation: {
        blob: "blob 7s infinite",
      },
      keyframes: {
        blob: {
          "0%": {
            transform: "translate(0px, 0px) scale(1)",
          },
          "33%": {
            transform: "translate(30px, -50px) scale(1.1)",
          },
          "66%": {
            transform: "translate(-20px, 20px) scale(0.9)",
          },
          "100%": {
            transform: "translate(0px, 0px) scale(1)",
          },
        },
      },
      colors: {
        "bookmark-light-blue": "#0EA5E9",
        "bookmark-blue": "#0369A1",
        "bookmark-dark-blue": "#164E63",
        "bookmark-grey": "#9194A2",
        "bookmark-white": "#f7f7f7",
      },
      scale: {
        30: "0.30",
      },
      container: {
        center: true,
        screens: {
          lg: "924px",
          xl: "924px",
          "2xl": "1124px",
        },
      },
      gridTemplateColumns: {
        "auto-fit": "repeat(auto-fit, minmax(280px, 384px))",
      },
      lineClamp: {
        4: "4",
        18: "18",
      },
    },
  },
  daisyui: {
    themes: false,
  },
  plugins: [
    require("@tailwindcss/forms"),
    require("daisyui")
  ],
};
