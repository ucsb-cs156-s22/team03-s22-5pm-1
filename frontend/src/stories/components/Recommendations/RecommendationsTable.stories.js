import React from 'react';

import { recommendationsFixtures } from 'fixtures/recommendationsFixtures';
import RecommendationsTable from 'main/components/Recommendations/RecommendationsTable';

export default {
    title: 'components/Recommendations/RecommendationsTable',
    component: RecommendationsTable
};

const Template = (args) => {
    return (
        <RecommendationsTable {...args} />
    )
};

export const Empty = Template.bind({});

Empty.args = {
    recommendations: []
};

export const ThreeRecommendations = Template.bind({});

ThreeRecommendations.args = {
    recommendations: recommendationsFixtures.threeRecommendations
};


