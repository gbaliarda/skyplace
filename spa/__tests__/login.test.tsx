import { render, screen, fireEvent, waitFor } from "@testing-library/react"
import "@testing-library/jest-dom"
import Swal from "sweetalert2"
import { useRouter } from 'next/router'

import Login from "../pages/login"
import { loginUser } from "../services/users"

jest.mock("../services/users") // to mock the `loginUser` function

// Override the default mock set in `jest.setup.js`
jest.mock('next/router', () => ({
  useRouter: jest.fn(),
}))

beforeEach(() => {
  // Custom mock for the `useRouter` hook, to be able to mock
  // both the `query` and `replace` properties for testing
  (useRouter as jest.Mock).mockImplementation(() => ({
    query: {},
    replace: jest.fn(),
  }))

  render(<Login />)
})

// Fills the form, otherwise it can't submitted
const fillForm = () => {
  const emailInput = screen.getByPlaceholderText("login.email")
  const passwordInput = screen.getByPlaceholderText("login.password")

  fireEvent.change(emailInput, { target: { value: "test@test.com" } })
  fireEvent.change(passwordInput, { target: { value: "1234" } })
}
 
test("renders correct page elements", () => {
  const heading = screen.getByRole("heading", { name: "login.signIn" })
  const emailInput = screen.getByPlaceholderText("login.email")
  const passwordInput = screen.getByPlaceholderText("login.password")
  const submitButton = screen.getByRole("button", { name: "login.signInButton" })
  
  expect(heading).toBeInTheDocument()

  expect(emailInput).toBeInTheDocument()
  expect(emailInput).toHaveAttribute("required")

  expect(passwordInput).toBeInTheDocument()
  expect(emailInput).toHaveAttribute("required")

  expect(submitButton).toBeInTheDocument()
})

test("renders loading spinner when submitting the form", async () => {
  const submitButton = screen.getByRole("button", { name: "login.signInButton" })
  fillForm()

  // Before submitting the form, the spinner should not be rendered
  expect(screen.queryByTestId("spinner")).toBeNull()

  fireEvent.click(submitButton)

  // When submitting the form, the spinner should appear
  await waitFor(() => screen.getByTestId("spinner"))

  // Whether or not the API call succeeds, the spinner should disappear
  expect(screen.queryByTestId("spinner")).toBeNull()
})

test("shows error message when API fails to authenticate", async () => {
  const submitButton = screen.getByRole("button", { name: "login.signInButton" })
  fillForm();

  // Mock the API call to fail with a custom error message
  (loginUser as jest.Mock).mockRejectedValueOnce([
    { cause: { description: "Invalid credentials" } }
  ])
  
  fireEvent.click(submitButton)

  // The API call should have been made, and the error message should be shown
  await waitFor(() => {
    expect(Swal.fire).toHaveBeenCalledWith({
      title: "login.signInError",
      text: "Invalid credentials",
      icon: "error",
    })
  })
})

test("redirects to home page when API call succeeds", async () => {
  const mockRouterReplace = jest.fn();

  (useRouter as jest.Mock).mockImplementation(() => ({
    query: {},
    replace: mockRouterReplace,
  }))

  const submitButton = screen.getByRole("button", { name: "login.signInButton" })
  fillForm();

  // Mock the API call to succeed
  (loginUser as jest.Mock).mockResolvedValueOnce({})

  fireEvent.click(submitButton)

  // The API call should have been made, and the user should be redirected
  await waitFor(() => {
    expect(mockRouterReplace).toHaveBeenCalledWith("/")
  })
})

test("redirects to `from` query param when API call succeeds", async () => {
  const fromPage = "/products?id=1"
  const mockRouterReplace = jest.fn();

  (useRouter as jest.Mock).mockImplementation(() => ({
    query: { from: fromPage },
    replace: mockRouterReplace,
  }))

  const submitButton = screen.getByRole("button", { name: "login.signInButton" })
  fillForm();

  (loginUser as jest.Mock).mockResolvedValueOnce({})

  fireEvent.click(submitButton)

  await waitFor(() => {
    expect(mockRouterReplace).toHaveBeenCalledWith(fromPage)
  })
})
