import { render, screen } from "@testing-library/react"
import "@testing-library/jest-dom"

import Product from "../pages/product"
import { useSellorderUrl, usePendingBuyOrder } from "../services/sellorders"
import { useUserUrl } from "../services/users"
import { useNft } from "../services/nfts"

jest.mock("../services/sellorders", () => ({
    useSellorderUrl: jest.fn().mockReturnValue({}),
    usePendingBuyOrder: jest.fn().mockReturnValue({}),
    useBuyOrders: jest.fn().mockReturnValue({}),
}))

jest.mock("../services/users", () => ({
    useUserUrl: jest.fn(),
}))

jest.mock("../services/nfts", () => ({
    useNft: jest.fn().mockReturnValue({
        nft: {
            id: 1,
            owner: "https://api.chaingear.io/users/1",
            recommendations: "https://api.chaingear.io/recommendations/1",
            image: "https://api.chaingear.io/images/1",
        },
    }),
    useRecommendedNfts: jest.fn().mockReturnValue({}),
}))

beforeEach(() => {
    // This will match the user mocked for the `useSession` hook
    ;(useUserUrl as jest.Mock).mockImplementation(() => ({
        user: {
            id: 1,
            username: "test",
        },
    }))
})

test("shows correct controls when user is not owner", () => {
    ;(useUserUrl as jest.Mock).mockImplementation(() => ({
        user: { id: 2 }, // not the owner
    }))
    render(<Product />)

    expect(screen.queryByText("product.sell")).toBeNull()
})

test("shows correct controls when user is owner and product is not for sale", () => {
    render(<Product />)

    const sellBtns = screen.getAllByText("product.sell")
    // One from the dropdown, one from the button
    expect(sellBtns).toHaveLength(2)

    expect(screen.getByText("product.delete")).toBeInTheDocument()
    expect(screen.getByText("product.notForSale")).toBeInTheDocument()
})

test("shows correct controls when user is owner and product is for sale", () => {
    ;(useSellorderUrl as jest.Mock).mockImplementation(() => ({
        sellorder: {
            id: 1,
            price: "1000000000000000000",
            buyorders: "https://api.chaingear.io/buyorders?sellorder=1",
        },
    }))
    ;(useNft as jest.Mock).mockImplementation(() => ({
        nft: {
            id: 1,
            owner: "https://api.chaingear.io/users/1",
            recommendations: "https://api.chaingear.io/recommendations/1",
            image: "https://api.chaingear.io/images/1",
            sellorder: "https://api.chaingear.io/sellorders/1",
        },
    }))
    render(<Product />)

    expect(screen.queryByText("product.sell")).toBeNull()
    expect(screen.queryByText("product.delete")).toBeNull()

    expect(screen.getByText("product.updateSell")).toBeInTheDocument()
    expect(screen.getByText("product.deleteSell")).toBeInTheDocument()

    // Price shouldn't be displayed in exponential notation
    expect(screen.getByText("1000000000000000000")).toBeInTheDocument()
})

test("shows correct controls when user is owner and product has a pending buy order", () => {
    ;(useNft as jest.Mock).mockImplementation(() => ({
        nft: {
            id: 1,
            owner: "https://api.chaingear.io/users/1",
            recommendations: "https://api.chaingear.io/recommendations/1",
            image: "https://api.chaingear.io/images/1",
            sellorder: "https://api.chaingear.io/sellorders/1",
        },
    }))
    ;(usePendingBuyOrder as jest.Mock).mockImplementation(() => ({
        pendingBuyOrder: {
            amount: 1000000000000000,
            offeredBy: "https://api.chaingear.io/users/1",
            sellorder: "https://api.chaingear.io/sellorder/1",
            status: "PENDING",
        },
    }))
    render(<Product />)

    expect(screen.queryByText("product.sell")).toBeNull()
    expect(screen.queryByText("product.delete")).toBeNull()
    expect(screen.queryByText("product.updateSell")).toBeNull()
    expect(screen.queryByText("product.deleteSell")).toBeNull()

    expect(screen.getByText("product.pendingOffer")).toBeInTheDocument()
})

test("shows correct controls when user is has to confirm the pending buy order", () => {
    ;(useNft as jest.Mock).mockImplementation(() => ({
        nft: {
            id: 1,
            owner: "https://api.chaingear.io/users/1",
            recommendations: "https://api.chaingear.io/recommendations/1",
            image: "https://api.chaingear.io/images/1",
            sellorder: "https://api.chaingear.io/sellorders/1",
        },
    }))
    ;(useUserUrl as jest.Mock).mockImplementation(() => ({
        user: { id: 1 }, // same as the mocked current user
    }))
    ;(usePendingBuyOrder as jest.Mock).mockImplementation(() => ({
        pendingBuyOrder: {
            amount: 1000000000000000,
            offeredBy: "https://api.chaingear.io/users/1",
            sellorder: "https://api.chaingear.io/sellorder/1",
            status: "PENDING",
        },
    }))
    render(<Product />)

    expect(screen.getByText("product.confirm")).toBeInTheDocument()
    expect(screen.getByText("product.transactionDetails")).toBeInTheDocument()

    const copyAddressBtn = screen.getAllByTestId("copy-to-clipboard")
    expect(copyAddressBtn).toHaveLength(2) // seller and buyer addresses
})
