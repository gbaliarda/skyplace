import { render, screen, fireEvent, waitFor } from "@testing-library/react"
import "@testing-library/jest-dom"
import { useRouter } from "next/router"
import { sendJson } from "../services/endpoints"
import Swal from "sweetalert2"

import Sell from "../pages/sell"

jest.mock("next-export-i18n", () => ({
  useTranslation: () => ({
    t: (key: string) => {
      const translations: Record<string, string> = {
        "sell.sellNft": "Sell an NFT",
        "sell.nftId": "NFT ID",
        "sell.nftContract": "NFT Contract",
        "sell.category": "Category",
        "sell.price": "Price",
        "sell.publish": "Publish",
        "errors.sellNft": "Error selling NFT",
        "errorCodes.22": "User is not nft owner",
      }

      return translations[key] || ""
    },
  }),
}))

jest.mock("../services/endpoints")

jest.mock("next/router", () => ({
  useRouter: jest.fn(),
}))

const fillForm = () => {
  const categoryInput = screen.getByRole("combobox", { name: "Category" })
  const priceInput = screen.getByRole("spinbutton", { name: "Price (ETH)" })

  fireEvent.change(categoryInput, { target: { value: "Art" } })
  fireEvent.change(priceInput, { target: { value: "1" } })
}

describe("Sell", () => {
  beforeEach(() => {
    ;(useRouter as jest.Mock).mockImplementation(() => ({
      query: {},
      replace: jest.fn(),
    }))
  })

  test("renders sell form", () => {
    render(<Sell />)
    const heading = screen.getByRole("heading", { name: "Sell an NFT" })
    const nftId = screen.getByText("NFT ID:")
    const nftContract = screen.getByText("NFT Contract:")
    const category = screen.getByLabelText("Category")
    const price = screen.getByLabelText("Price (ETH)")
    const publish = screen.getByRole("button", { name: "Publish" })

    // Assert the presence of form elements
    expect(heading).toBeInTheDocument()
    expect(nftId).toBeInTheDocument()
    expect(nftContract).toBeInTheDocument()
    expect(category).toBeInTheDocument()
    expect(price).toBeInTheDocument()
    expect(publish).toBeInTheDocument()
  })

  it("shows error message when api call fails", async () => {
    render(<Sell />)
    const submitButton = screen.getByRole("button", { name: "Publish" })
    fillForm()
    ;(sendJson as jest.Mock).mockRejectedValueOnce([
      { cause: { statusCode: 22, title: "Error selling NFT", text: "User is not nft owner" } },
    ])

    fireEvent.click(submitButton)

    await waitFor(() => {
      expect(Swal.fire).toHaveBeenCalledWith({
        title: "Error selling NFT",
        text: "User is not nft owner",
        icon: "error",
      })
    })
  })

  it("redirect to product page when API call succeeds", async () => {
    render(<Sell />)
    const productPage = "/product?id=1"
    const mockRouterReplace = jest.fn()

    ;(useRouter as jest.Mock).mockImplementation(() => ({
      query: { id: "1" },
      replace: mockRouterReplace,
    }))

    const submitButton = screen.getByRole("button", { name: "Publish" })

    fillForm()
    ;(sendJson as jest.Mock).mockResolvedValueOnce({})

    fireEvent.click(submitButton)

    await waitFor(() => {
      expect(mockRouterReplace).toHaveBeenCalledWith(productPage)
    })
  })
})
