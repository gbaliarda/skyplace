// Mock hooks and libs

jest.mock("next-export-i18n", () => ({
    useTranslation: () => ({
        // Mock the translation function to return the key itself
        t: (key) => key,
    }),
}))

jest.mock("next/router", () => ({
    useRouter: () => ({
        query: {},
        replace: jest.fn(),
        push: jest.fn(),
    }),
}))

jest.mock('sweetalert2', () => ({
    fire: jest.fn(),
}))

jest.mock("./hooks/useSession", () => ({
    // `__esModule: true` as `useSession` is a default export
    __esModule: true,
    // `default` as `useSession` is a default export
    default: jest.fn().mockReturnValue({
      userId: 1,
      user: "test",
      roles: ["user"],
      accessToken: "1234secretAccessToken",
      refreshToken: "1234secretRefreshToken",
    }),
}))

// Mock browser APIs

const mockStorage = {
    getItem: jest.fn().mockReturnValue("mockedValue"),
    setItem: jest.fn(),
    removeItem: jest.fn(),
    clear: jest.fn(),
}

Object.defineProperty(window, "localStorage", { value: mockStorage })
Object.defineProperty(window, "sessionStorage", { value: mockStorage })
// global.localStorage = mockStorage
// global.sessionStorage = mockStorage
