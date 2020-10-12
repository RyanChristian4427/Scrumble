import { Fragment, FunctionalComponent, h, VNode } from 'preact';
import { useEffect, useState } from 'preact/hooks';
import { getCurrentUrl, Route, route, Router, RouterOnChangeArgs } from 'preact-router';
import { useSelector, Provider } from 'react-redux';
import { PersistGate } from 'redux-persist/integration/react';
import Notifications from 'react-notify-toast';

import { TopBar } from 'components/Core/TopBar';
import Login from 'routes/Auth/login';
import Home from 'routes/Home';
import NotFound from 'routes/errors/404';
import Workspace from 'routes/Workspace';
import Sprint from 'routes/Sprint';
import redux, { RootState } from 'stores';

const App: FunctionalComponent = () => {
    const [notLoginPage, setNotLoginPage] = useState<boolean>(getCurrentUrl() != '/login');

    const handleRoute = (e: RouterOnChangeArgs): void => setNotLoginPage(e.url != '/login');

    return (
        <Fragment>
            <div id="app" class="bg-blue-100">
                <Provider store={redux.store}>
                    <PersistGate persistor={redux.persistor}>
                        <TopBar notLoginPage={notLoginPage} />
                        <Router onChange={handleRoute}>
                            <Route path="/login" component={Login} />
                            <AuthenticatedRoute path="/" component={Home} />
                            <AuthenticatedRoute path="/workspace/:workspaceId/:subPage?" component={Workspace} />
                            <AuthenticatedRoute
                                path="/workspace/:workspaceId/sprint/:sprintId/:subPage?"
                                component={Sprint}
                            />
                            <Route default component={NotFound} />
                        </Router>
                    </PersistGate>
                </Provider>
            </div>
            <Notifications />
            <div id="modal" />
        </Fragment>
    );
};

const AuthenticatedRoute = (props: { path: string; component: FunctionalComponent<any> }): VNode => {
    const { isAuthenticated } = useSelector((state: RootState) => state.auth);

    useEffect(() => {
        if (!isAuthenticated) route('/login', true);
    }, [isAuthenticated]);

    return <Route {...props} />;
};

export default App;
