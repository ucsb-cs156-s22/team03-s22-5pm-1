import React from 'react'
import { useBackend } from 'main/utils/useBackend'; // use prefix indicates a React Hook

import BasicLayout from "main/layouts/BasicLayout/BasicLayout";
import ArticlesTable from 'main/components/Articles/ArticlesTable';
import { useCurrentUser } from 'main/utils/currentUser' // use prefix indicates a React Hook

export default function ArticlesIndexPage() {

  const currentUser = useCurrentUser();

  const { data: articles, error: _error, status: _status } =
    useBackend(
      // Stryker disable next-line all : don't test internal caching of React Query
      ["/api/articles/all"],
            // Stryker disable next-line StringLiteral,ObjectLiteral : since "GET" is default, "" is an equivalent mutation
            { method: "GET", url: "/api/articles/all" },
      []
    );

  return (
    <BasicLayout>
      <div className="pt-2">
        <h1>UCSB Dining Commons</h1>
        <ArticlesTable articles={articles} currentUser={currentUser} />
      </div>
    </BasicLayout>
  )
}