import { Fragment, FunctionalComponent, h } from 'preact';
import { useEffect, useState } from 'preact/hooks';
import { notify } from 'react-notify-toast';

import { SprintCard } from 'components/Cards/sprint';
import { CreateOrEditSprint } from 'components/CreateOrEdit/sprint';
import { SprintFilter } from 'components/Filter/sprint';
import { Modal } from 'components/Modal';
import { sprints } from 'data';
import { Sprint, SprintStatus } from 'models/Sprint';
import { createSprint } from 'services/api/sprints';
import { errorColour, successColour } from 'services/notification/colours';
import { useStore } from 'stores';

import Backlog from './Backlog';

const SprintPlanning: FunctionalComponent = () => {
    const userLocationStore = useStore().userLocationStore;

    // For mobile
    const [isSprintView, setIsSprintView] = useState(false);

    const [showNewSprintModal, setShowNewSprintModal] = useState(false);
    const [sprintFilter, setSprintFilter] = useState(SprintStatus.active.toString());

    useEffect(() => {
        userLocationStore.setActiveSideBarItem(0);
    }, [userLocationStore]);

    const handleSprintCreation = async (newSprint: Sprint): Promise<void> => {
        return await createSprint(userLocationStore.currentWorkspace.id, newSprint).then((error) => {
            if (error) notify.show(error, 'error', 5000, errorColour);
            else {
                notify.show('New sprint created!', 'success', 5000, successColour);
                // TODO Need to do something with this eventually
                // getSprints(userLocationStore.currentWorkspace.id);
            }
        });
    };

    const updateSprintFilter = (filterFor: string): void => setSprintFilter(filterFor);

    return (
        <Fragment>
            {showNewSprintModal ? (
                <Modal
                    title="Create Sprint"
                    content={
                        <CreateOrEditSprint
                            submit={handleSprintCreation}
                            close={(): void => setShowNewSprintModal(false)}
                        />
                    }
                    close={(): void => setShowNewSprintModal(false)}
                />
            ) : null}

            <div class="flex">
                <div class={`w-11/12 md:w-1/2 md:block md:mr-4 ${!isSprintView ? '' : 'sm:hidden'}`}>
                    <Backlog />
                </div>
                <div
                    class={`md:border-l border-gray-300 w-11/12 md:w-1/2 md:block " ${isSprintView ? '' : 'sm:hidden'}`}
                >
                    <div class="create-bar">
                        <h1 class="md:ml-4 page-heading">Sprints</h1>
                        <button
                            onClick={(): void => setIsSprintView(!isSprintView)}
                            class="btn-create md:hidden my-auto mr-4"
                        >
                            Backlog
                        </button>
                        <button class="btn-create my-auto" onClick={(): void => setShowNewSprintModal(true)}>
                            New Sprint
                        </button>
                    </div>
                    <div class="md:ml-4">
                        <SprintFilter setFilter={updateSprintFilter} />
                    </div>
                    <div class="md:ml-4 rounded bg-white overflow-hidden shadow-lg">
                        {sprints.map((sprint, index) => {
                            if (sprintFilter === 'all' || sprint.status.toString() === sprintFilter) {
                                return (
                                    <SprintCard
                                        key={index}
                                        sprint={sprint}
                                        closed={sprint.status === SprintStatus.closed}
                                    />
                                );
                            }
                        })}
                    </div>
                </div>
            </div>
        </Fragment>
    );
};

export default SprintPlanning;
