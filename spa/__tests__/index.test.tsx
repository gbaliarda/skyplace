import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import "@testing-library/jest-dom"
import Home from '../pages';

import { useTranslation } from "next-export-i18n"
import { useRouter } from "next/router"
import { RouterContext } from "next/dist/shared/lib/router-context"

jest.mock('next-export-i18n', () => ({
    useTranslation: () => ({
        t: (key: string) => {
            const translations: Record<string, string> = {
                'index.discover': 'Discover',
                'index.create': 'Create',
                'index.explore': 'Explore',
            };

            return translations[key] || '';
        },
    }),
}));

jest.mock('next/router', () => ({
    useRouter: jest.fn(),
}));

beforeEach(() => {
    ;(useRouter as jest.Mock).mockImplementation(() => ({
        push: jest.fn(),
        query: {},
      }))
})



test('renders the discover message', () => {
    const { getByText } = render(<Home />);
    const discoverMessage = getByText('Discover');
    expect(discoverMessage).toBeInTheDocument();
});

test('renders the create button', () => {
    render(<Home />);
    const createButton = screen.getByRole('button', { name: 'Create' });
    expect(createButton).toBeInTheDocument();
});

test('renders the explore button', () => {
    render(<Home />);
    const exploreButton = screen.getByRole('button', { name: 'Explore' });
    expect(exploreButton).toBeInTheDocument();
});

test('redirects to the create page when the create button is clicked', () => {
    let router = useRouter()
    render(
        <RouterContext.Provider value={router}>
            <Home />
        </RouterContext.Provider>
    )

    const createButton = screen.getByRole('button', { name: 'Create' });

    fireEvent.click(createButton);

    const finalUrl = '/create'

    expect(router.push).toHaveBeenCalledWith(finalUrl, finalUrl, {
        locale: undefined,
        scroll: undefined,
        shallow: undefined,
        });
});

test('redirects to the explore page when the explore button is clicked', () => {
    let router = useRouter()
    render(
        <RouterContext.Provider value={router}>
            <Home />
        </RouterContext.Provider>
    )

    const exploreButton = screen.getByRole('button', { name: 'Explore' });

    fireEvent.click(exploreButton);

    const finalUrl = '/explore'

    expect(router.push).toHaveBeenCalledWith(finalUrl, finalUrl, {
        locale: undefined,
        scroll: undefined,
        shallow: undefined,
        });
});
