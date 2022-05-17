import { fireEvent, render, waitFor } from "@testing-library/react";
import { QueryClient, QueryClientProvider } from "react-query";
import { MemoryRouter } from "react-router-dom";
import OrganizationsIndexPage from "main/pages/Organizations/OrganizationsIndexPage";


import { apiCurrentUserFixtures } from "fixtures/currentUserFixtures";
import { systemInfoFixtures } from "fixtures/systemInfoFixtures";
import { organizationsFixtures } from "fixtures/organizationsFixtures"
import axios from "axios";
import AxiosMockAdapter from "axios-mock-adapter";
import _mockConsole from "jest-mock-console";


const mockToast = jest.fn();
jest.mock('react-toastify', () => {
    const originalModule = jest.requireActual('react-toastify');
    return {
        __esModule: true,
        ...originalModule,
        toast: (x) => mockToast(x)
    };
});

describe("OrganizationsIndexPage tests", () => {

    const axiosMock =new AxiosMockAdapter(axios);

    const testId = "OrganizationsTable";

    const setupUserOnly = () => {
        axiosMock.reset();
        axiosMock.resetHistory();
        axiosMock.onGet("/api/currentUser").reply(200, apiCurrentUserFixtures.userOnly);
        axiosMock.onGet("/api/systemInfo").reply(200, systemInfoFixtures.showingNeither);
    };

    const setupAdminUser = () => {
        axiosMock.reset();
        axiosMock.resetHistory();
        axiosMock.onGet("/api/currentUser").reply(200, apiCurrentUserFixtures.adminUser);
        axiosMock.onGet("/api/systemInfo").reply(200, systemInfoFixtures.showingNeither);
    };

    test("renders without crashing for regular user", () => {
        setupUserOnly();
        const queryClient = new QueryClient();
        axiosMock.onGet("/api/ucsborganization/all").reply(200, []);

        render(
            <QueryClientProvider client={queryClient}>
                <MemoryRouter>
                    <OrganizationsIndexPage />
                </MemoryRouter>
            </QueryClientProvider>
        );


    });

    test("renders without crashing for admin user", () => {
        setupAdminUser();
        const queryClient = new QueryClient();
        axiosMock.onGet("/api/ucsborganization/all").reply(200, []);

        render(
            <QueryClientProvider client={queryClient}>
                <MemoryRouter>
                    <OrganizationsIndexPage />
                </MemoryRouter>
            </QueryClientProvider>
        );


    });

    test("renders three organizations without crashing for regular user", async () => {
        setupUserOnly();
        const queryClient = new QueryClient();
        axiosMock.onGet("/api/ucsborganization/all").reply(200, organizationsFixtures.threeOrganizations);

        const { getByTestId } = render(
            <QueryClientProvider client={queryClient}>
                <MemoryRouter>
                    <OrganizationsIndexPage />
                </MemoryRouter>
            </QueryClientProvider>
        );

        await waitFor(  () => { expect(getByTestId(`${testId}-cell-row-0-col-orgCode`)).toHaveTextContent("ERSP"); } );
        expect(getByTestId(`${testId}-cell-row-1-col-orgCode`)).toHaveTextContent("EOP");
        expect(getByTestId(`${testId}-cell-row-2-col-orgCode`)).toHaveTextContent("EAP");

    });

    test("renders three organizations without crashing for admin user", async () => {
        setupAdminUser();
        const queryClient = new QueryClient();
        axiosMock.onGet("/api/ucsborganization/all").reply(200, organizationsFixtures.threeOrganizations);

        const { getByTestId } = render(
            <QueryClientProvider client={queryClient}>
                <MemoryRouter>
                    <OrganizationsIndexPage />
                </MemoryRouter>
            </QueryClientProvider>
        );

        await waitFor(() => { expect(getByTestId(`${testId}-cell-row-0-col-orgCode`)).toHaveTextContent("ERSP"); });
        expect(getByTestId(`${testId}-cell-row-1-col-orgCode`)).toHaveTextContent("EOP");
        expect(getByTestId(`${testId}-cell-row-2-col-orgCode`)).toHaveTextContent("EAP");

    });

    test("renders empty table when backend unavailable, user only", async () => {
        setupUserOnly();

        const queryClient = new QueryClient();
        axiosMock.onGet("/api/ucsborganization/all").timeout();

        const { queryByTestId, getByText } = render(
            <QueryClientProvider client={queryClient}>
                <MemoryRouter>
                    <OrganizationsIndexPage />
                </MemoryRouter>
            </QueryClientProvider>
        );

        await waitFor(() => { expect(axiosMock.history.get.length).toBeGreaterThanOrEqual(3); });

        const expectedHeaders = ['Org Code',  'Org Translation Short', 'Org Translation','Inactive?'];
    
        expectedHeaders.forEach((headerText) => {
          const header = getByText(headerText);
          expect(header).toBeInTheDocument();
        });

        expect(queryByTestId(`${testId}-cell-row-0-col-orgCode`)).not.toBeInTheDocument();
    });

    test("test what happens when you click delete, admin", async () => {
        setupAdminUser();

        const queryClient = new QueryClient();
        axiosMock.onGet("/api/ucsborganization/all").reply(200, organizationsFixtures.threeOrganizations);
        axiosMock.onDelete("/api/ucsborganization", {params: {orgCode: "ERSP"}}).reply(200, "Organization with id ERSP was deleted");


        const { getByTestId } = render(
            <QueryClientProvider client={queryClient}>
                <MemoryRouter>
                    <OrganizationsIndexPage />
                </MemoryRouter>
            </QueryClientProvider>
        );

        await waitFor(() => { expect(getByTestId(`${testId}-cell-row-0-col-orgCode`)).toBeInTheDocument(); });

       expect(getByTestId(`${testId}-cell-row-0-col-orgCode`)).toHaveTextContent("ERSP"); 


        const deleteButton = getByTestId(`${testId}-cell-row-0-col-Delete-button`);
        expect(deleteButton).toBeInTheDocument();
       
        fireEvent.click(deleteButton);

        await waitFor(() => { expect(mockToast).toBeCalledWith("Organization with id ERSP was deleted") });

    });

});


