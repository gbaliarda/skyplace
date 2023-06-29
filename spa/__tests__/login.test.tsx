import { render, screen } from "@testing-library/react"
import "@testing-library/jest-dom"

import Login from "../pages/login"
 
test("Renders the page heading", () => {
  render(<Login />)

  const heading = screen.getByRole("heading", {
    name: "login.signIn",
  })

  expect(heading).toBeInTheDocument()
})
