import OurTable, { ButtonColumn } from "main/components/OurTable";
import { useBackendMutation } from "main/utils/useBackend";
import { onDeleteSuccess } from "main/utils/UCSBDateUtils"
import { useNavigate } from "react-router-dom";
import { hasRole } from "main/utils/currentUser";


export function cellToAxiosParamsDelete(cell) {
    return {
        url: "/api/HelpRequest",
        method: "DELETE",
        params: {
            id: cell.row.values.id
        }
    }
}

export default function HelpRequestTable({ helpRequest, currentUser }) {

    const navigate = useNavigate();

    const editCallback = (cell) => {
        navigate(`/HelpRequest/edit/${cell.row.values.id}`)
    }

    // Stryker disable all : hard to test for query caching
    const deleteMutation = useBackendMutation(
        cellToAxiosParamsDelete,
        { onSuccess: onDeleteSuccess },
        ["/api/HelpRequest/all"]
    );
    // Stryker enable all 

    // Stryker disable next-line all : TODO try to make a good test for this
    const deleteCallback = async (cell) => { deleteMutation.mutate(cell); }

    const columns = [
        {
            Header: 'id',
            accessor: 'id', // accessor is the "key" in the data
        },
        {
            Header: 'Requester Email',
            accessor: 'requesterEmail',
        },
        {
            Header: 'Team ID',
            accessor: 'teamId',
        },
        {
            Header: 'Table or Breakout Room',
            accessor: 'tableOrBreakoutRoom',
        },
        {
            Header: 'Request Time',
            accessor: 'requestTime',
        },
        {
            Header: 'Explanation',
            accessor: 'explanation',
        },
        {
            Header: 'Solved',
            accessor: 'solved',
            accessor: (row, _rowIndex) => String(row.solved)
        }

    ];

    const columnsIfAdmin = [
        ...columns,
        ButtonColumn("Edit", "primary", editCallback, "HelpRequestTable"),
        ButtonColumn("Delete", "danger", deleteCallback, "HelpRequestTable")
    ];

    const columnsToDisplay = hasRole(currentUser, "ROLE_ADMIN") ? columnsIfAdmin : columns;

    return <OurTable
        data={helpRequest}
        columns={columnsToDisplay}
        testid={"HelpRequestTable"}
    />;
};