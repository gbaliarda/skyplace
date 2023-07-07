import {fireEvent, render, screen} from "@testing-library/react"
import "@testing-library/jest-dom"
import {useRouter} from "next/router"
import {RouterContext} from "next/dist/shared/lib/router-context"
import Register from "../pages/register";
jest.mock("../services/users")

jest.mock("next/router", () => ({
    useRouter: jest.fn(),
}))

beforeEach(() => {
    ;(useRouter as jest.Mock).mockImplementation(() => ({
        query: {},
        push: jest.fn(),
        replace: jest.fn()
    }))
})

test("renders correct page elements", () => {
    render(<Register/>)
    const heading = screen.getByRole("heading", { name: "register.createAccount" })
    const emailTextBox = screen.getByRole("textbox", { name: "register.email" })
    const walletAddressTextBox = screen.getByRole("textbox", { name: "register.walletAddress" })
    const walletChainComboBox = screen.getByRole("combobox", { name: "register.walletChain" })
    const usernameTextBox = screen.getByRole("textbox", { name: "register.username" })
    const createAccountButton = screen.getByRole("button", { name: "register.createAccount" })

    expect(heading).toBeInTheDocument()

    expect(emailTextBox).toBeInTheDocument()
    expect(emailTextBox).toHaveAttribute("required")
    expect(emailTextBox).toHaveAttribute("type", "email")

    expect(walletAddressTextBox).toBeInTheDocument()
    expect(walletAddressTextBox).toHaveAttribute("required")
    expect(walletAddressTextBox).toHaveAttribute("type", "text")

    expect(walletChainComboBox).toBeInTheDocument()
    expect(walletChainComboBox).toHaveAttribute("required")

    expect(usernameTextBox).toBeInTheDocument()
    expect(usernameTextBox).toHaveAttribute("required")
    expect(usernameTextBox).toHaveAttribute("type", "text")

    expect(createAccountButton).toBeInTheDocument()
    expect(createAccountButton).toHaveAttribute("type", "submit")
})

test("redirects to login page on login link click", () => {
    let router = useRouter()
    render(<RouterContext.Provider value={router}>
        <Register/>
    </RouterContext.Provider>)
    const loginRedirect = screen.getByRole("link", { name: "register.login" })
    const finalUrl = "/login"

    fireEvent.click(loginRedirect)
    expect(router.push).toHaveBeenCalledWith(finalUrl, finalUrl, {
        locale: undefined,
        scroll: undefined,
        shallow: undefined
    })
})