import OurTable, { ButtonColumn} from "main/components/OurTable";
import { useBackendMutation } from "main/utils/useBackend";
import {  onDeleteSuccess } from "main/utils/UCSBDateUtils"
// import { useNavigate } from "react-router-dom";
import { hasRole } from "main/utils/currentUser";


export function cellToAxiosParamsDelete(cell) {
    return {
        url: "/api/menuitemreview",
        method: "DELETE",
        params: {
            id: cell.row.values.id
        }
    }
}

export default function ReviewsTable({ reviews, currentUser }) {

    // const navigate = useNavigate();

    // const editCallback = (cell) => {
    //     navigate(`/ucsbdates/edit/${cell.row.values.id}`)
    // }

    // Stryker disable all : hard to test for query caching
    const deleteMutation = useBackendMutation(
        cellToAxiosParamsDelete,
        { onSuccess: onDeleteSuccess },
        ["/api/menuitemreview/all"]
    );
    // Stryker enable all 

    // Stryker disable next-line all : TODO try to make a good test for this
    const deleteCallback = async (cell) => { deleteMutation.mutate(cell); }

    const columns = [
        {
            Header: 'ID',
            accessor: 'id',
        },
        {
            Header: 'Item ID',
            accessor: 'itemId', 
        },
        {
            Header: 'Reviewer Email',
            accessor: 'reviewerEmail',
        },
        {
            Header: 'Local Date/Time',
            accessor: 'dateReviewed'
        },
        {
            Header: 'Stars',
            id: 'stars', // needed for tests
            accessor: (row, _rowIndex) => String(row.stars) // hack needed for boolean values to show up

        },
        {
            Header: 'Comments',
            id: 'comments',
            accessor: (row, _rowIndex) => String(row.comments) // needed for tests
        }
    ];

    const testid = "ReviewsTable";

    const columnsIfAdmin = [
        ...columns,
        // ButtonColumn("Edit", "primary", editCallback, testid),
        ButtonColumn("Delete", "danger", deleteCallback, testid)
    ];

    const columnsToDisplay = hasRole(currentUser, "ROLE_ADMIN") ? columnsIfAdmin : columns;

    return <OurTable
        data={reviews}
        columns={columnsToDisplay}
        testid={testid}
    />;
};