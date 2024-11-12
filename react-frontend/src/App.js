import './App.css';
import React, { useState, useEffect } from 'react'
import { Navigate,BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import VerifyCodePage from './component/email_verification/VerifyCodePage';
import AuthPage from './component/signup_login/AuthPage';
import ForgotPasswordPage from './component/forgot_password/ForgotPasswordPage';
import HomePage from './component/home/Home';
import api from './api/axiosConfig'
import Reviews from './component/reviews/Reviews'

function App() {


  
const [faculties, setMovies] = useState()
const [faculty, setMovie] = useState()
const [reviews, setReviews] = useState([])

const getMovies = async () => {
  try {
    const token = localStorage.getItem('token')

    const response = await api.get(
      '/user/faculties',{
      headers: {
        Authorization: `Bearer ${token}`, // Include the token in the Authorization header
      },
    }
    )

    setMovies(response.data.faculties)
  } catch (err) {
    console.log(err)
  }
}

const getMovieData = async facultyId => {
  try {
    const token = localStorage.getItem('token')

    const response = await api.get(
      `/user/faculties/${facultyId}`, {
      headers: {
        Authorization: `Bearer ${token}`, // Include the token in the Authorization header
      },
    }
    )

    const singleMovie = response.data.faculty

    setMovie(singleMovie)

    setReviews(singleMovie.reviewIds)
  } catch (error) {
    console.error(error)
  }
}

useEffect(() => {
  getMovies()
}, [])



 return (
<Router>
  <Routes>
    <Route path='/' element={<AuthPage />} />
    <Route path='/verify' element={<VerifyCodePage />} />
    <Route path='/forgotPassword' element={<ForgotPasswordPage />} />
       <Route path='/home' element={<HomePage faculties={faculties} />}></Route>
      <Route path="/Reviews/:facultyId" element ={<Reviews getMovieData = {getMovieData} faculty={faculty} reviews ={reviews} setReviews = {setReviews} />}></Route>
       
  </Routes>
</Router>

)
}

export default App;
