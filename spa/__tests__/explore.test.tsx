import { render, screen } from "@testing-library/react"
import "@testing-library/jest-dom"
import { Links } from "parse-link-header"
import { useTranslation } from 'next-export-i18n'
import queryString from "query-string"
import Explore from "../pages/explore"
import { NftsURL, useNfts } from "../services/nfts"
import ExploreContent from "../components/molecules/ExploreContent"
import Nft from "../types/Nft"
import Paginator from "../components/molecules/Paginator"
import { useRouter } from "next/router"

const useNftsResponse = {
    nfts: [{
        "chain": "Ethereum",
        "collection": "asdd",
        "contractAddr": "0xabcdef0123456789ABCDEF0123456789abcdef01",
        "description": "asdasd",
        "favorites": 2,
        "id": 5,
        "nftId": 1234,
        "nftName": "321312313",
        "image": new URL("https://test.com/image"),
        "owner": new URL("https://test.com/owner"),
        "recommendations": new URL("https://test.com/recommendations"),
        "sellorder": new URL("https://test.com/sellorder"),
        "purchases": new URL("https://test.com/purchases"),
        "isDeleted": false,
    },{
        "chain": "Ethereum",
        "collection": "asd",
        "contractAddr": "0xabcdef0123456789ABCDEF0123456789abcdef01",
        "description": "asd",
        "favorites": 1,
        "id": 1,
        "nftId": 123,
        "nftName": "asd",
        "image": new URL("https://test.com/image"),
        "owner": new URL("https://test.com/owner"),
        "recommendations": new URL("https://test.com/recommendations"),
        "sellorder": new URL("https://test.com/sellorder"),
        "purchases": new URL("https://test.com/purchases"),
        "isDeleted": false,
    }] as Nft[],
    total: 2,
    totalPages: 1,
    links: undefined, 
    loading: false, 
    error: undefined, 
    refetchData: jest.fn()
}

jest.mock("../services/nfts", () => ({
    useNfts: jest.fn(),
}))

jest.mock('next-export-i18n', () => ({
    useTranslation: jest.fn()
}))

jest.mock("next/router", () => ({
    useRouter: jest.fn(),
}))

jest.spyOn(queryString, 'stringify').mockImplementation(jest.fn());

describe("Explore page", () => {
    beforeEach(() => {
        ;(useNfts as jest.Mock).mockImplementation((url: NftsURL) => (useNftsResponse))
        ;(useTranslation as jest.Mock).mockImplementation(() => ({
            t: jest.fn((key: string, variables?: Record<string, string>) => {
                if (variables)
                    return `${key} ${JSON.stringify(variables)}`;
                return key;
            })
        }))
        ;(useRouter as jest.Mock).mockImplementation(() => ({
            push: jest.fn(),
            pathname: "/",
            query: {},
        }))
    })

    it('Should render the filters', () => {
        render(<Explore />)

        const filters = screen.getByTestId("filters")

        expect(filters).toBeVisible()
    })

    it('Should render the pagination', () => {
        render(<Explore />)
        render(<ExploreContent nfts={useNftsResponse.nfts} updateUrl={jest.fn()} amountPages={1} sortDefaultValue={""} setSort={jest.fn()} />)
        render(<Paginator links={{} as Links} updateUrl={jest.fn()} amountPages={1} />)

        const pagination = screen.getByTestId("pagination")

        expect(pagination).toBeVisible()
    })

    it('Should render correct amount of nfts', () => {
        render(<Explore />)

        const amountNfts = screen.getByTestId("amountNfts")

        expect(amountNfts).toHaveTextContent(useNftsResponse.total.toString())
    })
})