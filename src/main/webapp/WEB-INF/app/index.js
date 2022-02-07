import React from 'react'
import ReactDOM from 'react-dom'
import {
  BrowserRouter as Router,
  Route,
  Switch,
} from "react-router-dom";
import './app.css'
import PublicView from './views/public/PublicView'
import Home from "app/views/home/Home";
import { AuthProvider } from "./shared/useAuth";
import useGlobals, { GlobalsProvider } from "app/shared/useGlobals";
import Logout from "app/views/logout/Logout";


function App() {
  return (
    <GlobalsProvider>
      <RequireGlobals>
        <Router>
          <AuthProvider>
            <Switch>
              <Route exact path="/">
                <PublicView />
              </Route>
              <Route exact path="/logout">
                <Logout />
              </Route>
              <Route>
                <Home />
              </Route>
            </Switch>
          </AuthProvider>
        </Router>
      </RequireGlobals>
    </GlobalsProvider>
  )
}

/**
 * Prevents the rendering of our application until globals have been loaded.
 */
function RequireGlobals({ children }) {
  const globals = useGlobals()

  if (!globals) {
    return null
  }
  return (
    <React.Fragment>
      {children}
    </React.Fragment>
  )
}

ReactDOM.render(<App />, document.getElementById('app'))
