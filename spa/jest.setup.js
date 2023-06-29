// Mock hooks

jest.mock("next-export-i18n", () => ({
    useTranslation: () => ({
        // Mock the translation function to return the key itself
        t: (key) => key,
    }),
}));

jest.mock("next/router", () => ({
    useRouter: () => ({
        // Mock the router to return an empty query object
        query: {},
    }),
}));

// Mock browser APIs

const mockStorage = {
    getItem: jest.fn().mockReturnValue("mockedValue"),
    setItem: jest.fn(),
    removeItem: jest.fn(),
    clear: jest.fn(),
}

global.localStorage = mockStorage
global.sessionStorage = mockStorage
