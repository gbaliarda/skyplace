import { render, screen, fireEvent, waitFor } from "@testing-library/react"
import "@testing-library/jest-dom"
import Swal from "sweetalert2"
import { useRouter } from 'next/router'

import CreateReview from "../pages/review"
import { useUser } from "../services/users"
import { sendJson } from "../services/endpoints"

// Override the default mock set in `jest.setup.js`
jest.mock('next/router', () => ({
  useRouter: jest.fn(),
}))

jest.mock("../services/users", () => ({
  useUser: jest.fn(),
}))

jest.mock("../services/endpoints")

beforeEach(() => {
  // Custom mock for the `useRouter` hook, to be able to mock
  // both the `query` and `replace` properties for testing
  (useRouter as jest.Mock).mockImplementation(() => ({
    query: { id: "1" },
    replace: jest.fn(),
  }));

  (useUser as jest.Mock).mockImplementation(() => ({
    user: {
      id: 1,
      username: "test",
      wallet: "0x123456789abcdefbdac4123512321",
      email: "test@test.com",
    },
    loading: false,
  }));
})

test("renders correct page elements", () => {
  render(<CreateReview />)

  const heading = screen.getByRole("heading", { name: "reviews.create" })
  const stars = screen.getAllByRole("radio")
  const submitButton = screen.getByRole("button", { name: "reviews.create" })

  expect(heading).toBeInTheDocument()
  // stars[0] is hidden, stars[1] is the first star
  expect(stars).toHaveLength(6)
  expect(submitButton).toBeInTheDocument()
})

test("error loading reviewee", () => {
  (useUser as jest.Mock).mockImplementation(() => ({
    errors: ["error"]
  }));

  render(<CreateReview />)

  expect(screen.getByText("reviews.errorReviewee")).toBeInTheDocument()
})

test("no `id` query param", () => {
  (useRouter as jest.Mock).mockImplementation(() => ({
    query: {},
    replace: jest.fn(),
  }));

  render(<CreateReview />)

  expect(screen.getByText("404.pageNotFound")).toBeInTheDocument()
})

test("error when submitting without rating", () => {
  render(<CreateReview />)

  const submitButton = screen.getByRole("button", { name: "reviews.create" })
  fireEvent.click(submitButton)

  expect(Swal.fire).toHaveBeenCalledWith({
    title: "errors.createReview",
    text: "errors.incompleteReview",
    icon: "error",
  })
})

test("invalid title when submitting", async () => {
  render(<CreateReview />)

  const submitButton = screen.getByRole("button", { name: "reviews.create" })
  const stars = screen.getAllByRole("radio")

  fireEvent.click(stars[5]); // 5 stars
  
  (sendJson as jest.Mock).mockRejectedValueOnce([{ cause: { field: "" } }])

  fireEvent.click(submitButton)

  await waitFor(() => {
    expect(Swal.fire).toHaveBeenCalledWith({
      title: "errors.createReview",
      text: "errors.invalidField",
      icon: "error",
    })
  })
})

test("submits review correctly", async () => {
  render(<CreateReview />)

  const submitButton = screen.getByRole("button", { name: "reviews.create" })
  const stars = screen.getAllByRole("radio")

  fireEvent.click(stars[5]); // 5 stars
  
  (sendJson as jest.Mock).mockResolvedValueOnce({})

  fireEvent.click(submitButton)

  await waitFor(() => {
    expect(Swal.fire).toHaveBeenCalledWith({
      title: "reviews.createSuccess",
      icon: "success",
    })
  })
})
