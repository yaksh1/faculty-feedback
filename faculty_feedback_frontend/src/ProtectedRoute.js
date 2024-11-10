// ProtectedRoute.js
import React from 'react'
import { Route, Navigate } from 'react-router-dom'

const ProtectedRoute = ({ component: Component, isAuthenticated, ...rest }) => (
  <Route
    {...rest}
    render={props =>
      isAuthenticated ? <Component {...props} /> : <Navigate to='/' replace />
    }
  />
)

export default ProtectedRoute
