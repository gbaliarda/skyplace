import {fireEvent, render, screen} from "@testing-library/react"
import "@testing-library/jest-dom"
import userEvent from "@testing-library/user-event"
import {useRouter} from "next/router"
import Profile from "../pages/profile";
import {useUser} from "../services/users";
import ProfileDescription from "../components/molecules/profile/ProfileDescription";
import {useReviews} from "../services/reviews";
import {RouterContext} from "next/dist/shared/lib/router-context";

jest.mock("next/router", () => ({
    useRouter: jest.fn(),
}))

jest.mock("../services/users", () => ({
    useUser: jest.fn(),
}))

jest.mock("../services/endpoints")

jest.mock("../services/reviews", () => ({
    useReviews: jest.fn()
}))

beforeEach(() => {
    // Custom mock for the `useRouter` hook, to be able to mock
    // both the `query` and `replace` properties for testing
    ;(useRouter as jest.Mock).mockImplementation(() => ({
        push: jest.fn(),
        query: { id: "1", tab: "inventory" }
    }))
    ;(useUser as jest.Mock).mockImplementation(() => ({
        user: {
            id: 1,
            username: "test",
            wallet: "0x123456789abcdefbdac4123512321",
            email: "test@test.com",
        },
        loading: false,
    }))
    ;(useReviews as jest.Mock).mockImplementation(() => ({
        reviewsInfo: {
            score: 5,
            ratings: [
                { star: 5, score: 100 },
                { star: 4, score: 0 },
                { star: 3, score: 0 },
                { star: 2, score: 0 },
                { star: 1, score: 0 }
            ],
            reviews: [{
                id: 1,
                self: "",
                score: 5,
                title: "Title of review test",
                comments: "Comments of review test",
                reviewer: '',
                reviewee: ''
            }]
        },
        total: 1,
        totalPages: 1,
        loading: false
    }))
})

test("click on review count brings to review page", () => {
    let router = useRouter()
    render(<RouterContext.Provider value={router}>
        <ProfileDescription userId={1}/>
    </RouterContext.Provider>)
    const userData = useUser(1)
    const reviewCount = screen.getByRole("link", { name: "profile.reviewAmount" })
    const finalUrl = `/profile?id=${userData.user?.id}&tab=reviews`

    fireEvent.click(reviewCount)
    expect(router.push).toHaveBeenCalledWith(
        finalUrl,
        finalUrl,
        { locale: undefined, scroll: undefined, shallow: undefined }
    )
})

/*
test("click on tab button changes the current tab", () => {
    let router = useRouter()
    render(<RouterContext.Provider value={router}>
        <Profile />
    </RouterContext.Provider>)
    // Initial tab is inventory
    const sellingTabButton = screen.getByTestId('tab.selling')

    fireEvent.click(sellingTabButton)
    expect(useRouter().query.tab).toEqual("selling")
})
 */

test("copies wallet address on mouse click", () => {
    render(<Profile />)
    render(<ProfileDescription userId={1} />)
    const user = userEvent.setup()
    const userData = useUser(1)

    let copiedValue = ""
    const walletButton = screen.getByRole("button", { name: "profile.walletIcon " + userData.user?.wallet })

    user.click(walletButton)
    user.paste(copiedValue).then(() => expect(copiedValue).toEqual(userData.user?.wallet))
})

test("renders correct page elements", () => {
    render(<ProfileDescription userId={1} />)
    const user = useUser(1)

    const profileName = screen.getByText(user.user === undefined ? '' : user.user.username)
    const email = screen.getByText(user.user === undefined ? '' : user.user.email)
    const wallet = screen.getByText(user.user === undefined ? '' : user.user.wallet)

    expect(profileName).toBeInTheDocument()
    expect(email).toBeInTheDocument()
    expect(wallet).toBeInTheDocument()
})

test("renders loading spinner on tab content not loaded", async () => {
    ;(useRouter as jest.Mock).mockImplementation(() => ({
        isReady: false,
        pathname: '/',
        push: jest.fn(),
        query: { id: "1", tab: "inventory" }
    }))
    render(<Profile />)

    const spinner = screen.getByTestId("spinner")
    expect(spinner).toBeInTheDocument()
})