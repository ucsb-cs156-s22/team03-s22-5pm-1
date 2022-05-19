import React from 'react';

import MenuItemTable from "main/components/MenuItem/MenuItemTable";
import { menuitemFixtures } from 'fixtures/menuitemFixtures';

export default {
    title: 'components/MenuItem/MenuItemTable',
    component: MenuItemTable
};

const Template = (args) => {
    return (
        <MenuItemTable {...args} />
    )
};

export const Empty = Template.bind({});

Empty.args = {
    menuitem: []
};

export const ThreeMenuItem = Template.bind({});

ThreeMenuItem.args = {
    menuitem: menuitemFixtures.threemenuitem
};


