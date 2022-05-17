import React from 'react'
import { useBackend } from 'main/utils/useBackend'; // use prefix indicates a React Hook

import BasicLayout from "main/layouts/BasicLayout/BasicLayout";
import ReviewsTable from 'main/components/Reviews/ReviewsTable';
import { useCurrentUser } from 'main/utils/currentUser' // use prefix indicates a React Hook

export default function ReviewsIndexPage() {

  const currentUser = useCurrentUser();

  const { data: reviews, error: _error, status: _status } =
    useBackend(
      // Stryker disable next-line all : don't test internal caching of React Query
      ["/api/menuitemreview/all"],
            // Stryker disable next-line StringLiteral,ObjectLiteral : since "GET" is default, "" is an equivalent mutation
            { method: "GET", url: "/api/menuitemreview/all" },
      []
    );

  return (
    <BasicLayout>
      <div className="pt-2">
        <h1>Menu Item Reviews</h1>
        <ReviewsTable reviews={reviews} currentUser={currentUser} />
      </div>
    </BasicLayout>
  )
}