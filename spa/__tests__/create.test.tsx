import { fireEvent, render, screen, waitFor } from "@testing-library/react"
import "@testing-library/jest-dom"
import { useRouter } from "next/router"
import Create from "../pages/create"
import { fetcher } from "../services/endpoints"
import Swal from "sweetalert2"

jest.mock("../services/endpoints") 

jest.mock("next/router", () => ({
    useRouter: jest.fn(),
}))

const fillForm = () => {
    const nameInput = screen.getByRole("textbox", {name: "create.name"})
    const idInput = screen.getByRole("spinbutton", {name: "Id"})
    const contractInput = screen.getByRole("textbox", {name: "create.contract"})
    const collectionInput = screen.getByRole("textbox", {name: "create.collection"})
    const imageInput = screen.getByTestId("create-image-input")
  
    fireEvent.change(nameInput, { target: { value: "Space Ape" } })
    fireEvent.change(idInput, { target: { value: "123" } })
    fireEvent.change(contractInput, { target: { value: "0xabcdef0123456789ABCDEF0123456789abcdef01" } })
    fireEvent.change(collectionInput, { target: { value: "Crypto Apes" } })
    const image = new File(['111'], 'image.png', { type: 'image/png' });
    Object.defineProperty(imageInput, 'files', {
        value: [image],
      });

    fireEvent.change(imageInput);
    fireEvent.input(imageInput)
}

describe("Create page", () => {
    beforeEach(() => {
        ;(useRouter as jest.Mock).mockImplementation(() => ({
          query: {},
          replace: jest.fn(),
        }))
    })

    it("Should render all input elements", () => {
        render(<Create />)
        const nameInput = screen.getByRole("textbox", {name: "create.name"})
        const idInput = screen.getByRole("spinbutton", {name: "Id"})
        const contractInput = screen.getByRole("textbox", {name: "create.contract"})
        const collectionInput = screen.getByRole("textbox", {name: "create.collection"})
        const imageInput = screen.getByTestId("create-image-input")
        const descriptionInputt = screen.getByRole("textbox", {name: "create.description"})
        const submitButton = screen.getByRole("button", { name: "create.publish" })

        expect(nameInput).toBeVisible()
        expect(nameInput).toHaveAttribute("required")
        
        expect(idInput).toBeVisible()
        expect(idInput).toHaveAttribute("required")
        expect(idInput).toHaveAttribute("type", "number")
        
        expect(contractInput).toBeVisible()
        expect(contractInput).toHaveAttribute("required")

        expect(collectionInput).toBeVisible()
        expect(collectionInput).toHaveAttribute("required")
        
        expect(imageInput).toBeVisible()
        expect(imageInput).toHaveAttribute("required")
        expect(imageInput).toHaveAttribute("type", "file")

        expect(descriptionInputt).toBeVisible()

        expect(submitButton).toBeVisible()
    })

    it("shows error message when API fails to create", async () => {
        render(<Create />)
        const createForm = screen.getByTestId("create-form")
        fillForm()
      
        ;(fetcher as jest.Mock).mockRejectedValueOnce([
          { cause: { description: "Nft already exists", errorCode: "11", field: "/" } },
        ])
      
        fireEvent.submit(createForm)
      
        await waitFor(() => {
          expect(Swal.fire).toHaveBeenCalledWith({
            title: "errors.createNft",
            text: "create.nftAlreadyExists",
            icon: "error",
          })
        })
    })
    
    it("redirect to product page when API call succeeds", async () => {
        render(<Create />)
        const mockRouterPush = jest.fn()
        
        ;(useRouter as jest.Mock).mockImplementation(() => ({
          query: {},
          push: mockRouterPush,
        }))

        const mockSwalFire = jest.fn().mockImplementation(() => Promise.resolve())
        ;(Swal.fire as jest.Mock).mockImplementation(mockSwalFire);

        const headers = new Headers()
        headers.append("Location", "http://localhost:8080/api/nfts/16")
        ;(fetcher as jest.Mock).mockResolvedValueOnce({ headers })
      
        fillForm()
        
        const createForm = screen.getByTestId("create-form")
        fireEvent.submit(createForm)
      
        await waitFor(() => {
            expect(Swal.fire).toHaveBeenCalledWith({
              title: "create.createSuccess",
              icon: "success",
            })
        })
    })
})