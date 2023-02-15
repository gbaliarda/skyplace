import { describe, expect } from "@jest/globals"
import { genericFetcher } from "../services/endpoints"
import mockHTTPResponse, { HeadersMock } from "./mocks/HTTPResponseMock"

describe("GenericFetcher", () => {
  const endpoint = ["https://myapi.com", "/endpoint"] as [string, string]

  beforeAll(() => {
    const LocalStorageMock = require("./mocks/LocalStorageMock").default
    global.localStorage = new LocalStorageMock() // mock localStorage
    global.sessionStorage = new LocalStorageMock() // mock sessionStorage
    // @ts-ignore
    global.navigator = { language: "en" } // mock navigator
  })

  it("Should return nothing from a 204 response", async () => {
    mockHTTPResponse(204)
    const response = await genericFetcher(endpoint)
    expect(response).toBe(undefined)
  })

  it("Should return the body from a 200 response", async () => {
    const body = { foo: "bar" }
    mockHTTPResponse(200, body)
    const response = await genericFetcher(endpoint)
    expect(response).toEqual(body)
  })

  it("Should throw an error if the response is not ok", async () => {
    mockHTTPResponse(400)
    await expect(genericFetcher(endpoint)).rejects.toThrow()
    mockHTTPResponse(500)
    await expect(genericFetcher(endpoint)).rejects.toThrow()
  })

  it("Should return the headers if withHeaders is true", async () => {
    const headers = new HeadersMock()
    headers.set("x-total-pages", "1")
    mockHTTPResponse(200, {}, headers)
    const response = await genericFetcher(endpoint, { method: "GET" }, true)
    expect(response.headers).toBeDefined()
    expect(response.headers.get("x-total-pages")).toBe("1")
  })
})
