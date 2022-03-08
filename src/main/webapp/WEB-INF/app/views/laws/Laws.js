import React, { useEffect } from 'react'
import {
  Route,
  Switch,
  useLocation
} from "react-router-dom";
import LawSearch from "app/views/laws/LawSearch";
import SpecifiedLaw from "app/views/laws/SpecifiedLaw";
import LawUpdates from "app/views/laws/LawUpdates";
import ContentContainer from "app/shared/ContentContainer";

export default function Laws({ setHeaderText }) {
  const location = useLocation()

  useEffect(() => {
    if (location.pathname === '/laws') {
      setHeaderText("Search NYS Laws")
    }
    if (location.pathname === '/laws/updates') {
      setHeaderText("Search NYS Law Updates")
    }
  }, [ location ])

  return (
    <ContentContainer>
      <Switch>
        <Route exact path="/laws">
          <LawSearch />
        </Route>
        <Route exact path="/laws/updates">
          <LawUpdates />
        </Route>
        <Route path="/laws/:lawId">
          <SpecifiedLaw setHeaderText={setHeaderText} />
        </Route>
      </Switch>
    </ContentContainer>
  )
}