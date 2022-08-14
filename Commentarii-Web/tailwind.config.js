module.exports = {
  mode: "jit",
  content: [
    "./dist/**/*.php"
  ],
  darkmode: "class",
  theme: {
    extend: {},
  },
  plugins: [require('@tailwindcss/forms')],
}
