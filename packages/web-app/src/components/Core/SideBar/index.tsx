import { FunctionalComponent, h } from 'preact';
import { useState } from 'preact/hooks';
import { useDispatch, useSelector } from 'react-redux';
import { Menu, X } from 'preact-feather';

import { RootState } from 'stores';
import { reduxSetActiveSideBarMenuItem } from 'stores/userLocationStore';

import SideBarLinkItem from './SideBarLinkItem';

export interface SideBarLink {
    label: string;
    icon: string;
    path: string;
}

interface IProps {
    links: SideBarLink[];
}

const SideBar: FunctionalComponent<IProps> = (props: IProps) => {
    const dispatch = useDispatch();
    const { activeSideBarItem } = useSelector((state: RootState) => state.userLocation);

    const [isOpen, setIsOpen] = useState(false);

    const listItemOnClick = (index: number): void => {
        dispatch(reduxSetActiveSideBarMenuItem(index));
    };

    const sideBarLinks = props.links.map((menuItem, index) => {
        return (
            <SideBarLinkItem
                key={index}
                menuItem={menuItem}
                index={index}
                isOpen={isOpen}
                active={activeSideBarItem === index}
                onClick={listItemOnClick}
            />
        );
    });

    return (
        <div class={`sidebar ${isOpen ? 'w-48' : 'w-12'}`}>
            <ul class="list-reset flex flex-col text-left">
                <li onClick={(): void => setIsOpen(!isOpen)} class="side-nav-link border-b border-deep-space-sparkle">
                    <div class="flex items-start items-baseline min-h-12">
                        <div class={`my-auto ml-2 ${!isOpen ? 'block' : 'hidden'}`}>
                            <Menu size={20} />
                        </div>
                        <div class={`my-auto ml-2 ${isOpen ? 'block' : 'hidden'}`}>
                            <X size={20} />
                        </div>
                        <p class={`ml-3 my-auto ${isOpen ? 'block' : 'hidden'}`}>Close sidebar</p>
                    </div>
                </li>
                {sideBarLinks}
            </ul>
        </div>
    );
};

export default SideBar;
