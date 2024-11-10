import React, { useState } from 'react';
import './AuthPage.css';
import VerifyCodePage from '../email_verification/VerifyCodePage'
import '../utils/modal.css'
import { Lottie } from 'lottie-react'
import errorAnimation from '../../assets/animations/error.lottie'
import { DotLottieReact } from '@lottiefiles/dotlottie-react'
import { useNavigate } from 'react-router-dom' // Import useNavigate
import Reviews from '../reviews/Reviews';



function AuthPage () {
  const [isSignUp, setIsSignUp] = useState(false)
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [message, setMessage] = useState('')
  const [loading, setLoading] = useState(false) // New loading state
  const [showVerification, setShowVerification] = useState(false) // New state for verification page
  const [showErrorModal, setShowErrorModal] = useState(false); // State for error modal
  const [errorMessage, setErrorMessage] = useState(''); // State to hold error message
  const [showHomePage,setShowHomePage] = useState(false); // State
  const navigate = useNavigate() // Initialize navigate

  

  const handleToggle = () => {
    setIsSignUp(!isSignUp)
    setMessage('')
  }
  // Modify the onVerified call in VerifyCodePage
const handleVerified = () => {
  setShowVerification(false)
  setIsSignUp(false) // This will switch to the login view
  }

 
  const handleSubmit = async e => {
    e.preventDefault()
    setLoading(true)

    const url = isSignUp
      ? 'http://localhost:8080/auth/register'
      : 'http://localhost:8080/auth/login'
    const payload = isSignUp ? { email, password } : { email, password }

    try {
      const response = await fetch(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(payload)
      })

      const data = await response.json()



      if (response.ok) {
        if (isSignUp) {
          setShowVerification(true)
        } else {
          localStorage.setItem('token', data.token)
          console.log(data.token)
          setShowHomePage(true)
        }
      } else {
        if (data.errorCode === 'USER_ALREADY_EXISTS') {
          setErrorMessage("User Already exists with this email id."); // Set the error message
          setShowErrorModal(true); // Show the error modal
        }else if (data.errorCode === 'INVALID_PASSWORD') {
          setErrorMessage("Password is weak,please use a strong password."); // Set the error message
          setShowErrorModal(true); // Show the error modal
        }else if (data.errorCode === 'INVALID_EMAIL') {
          setErrorMessage("Email is not registered with MITWPU."); // Set the error message
          setShowErrorModal(true); // Show the error modal
        }else if (data.errorCode === 'USER_NOT_FOUND') {
          setErrorMessage("User does not exists with this email id."); // Set the error message
          setShowErrorModal(true); // Show the error modal
        }else if (data.errorCode === 'ACCOUNT_NOT_VERIFIED') {
          setErrorMessage("Account not verified,please verify your account."); // Set the error message
          setShowErrorModal(true); // Show the error modal
        }else if (data.errorCode === 'BAD_CREDENTIALS') {
          setErrorMessage("Wrong password or email,please check your credentials."); // Set the error message
          setShowErrorModal(true); // Show the error modal
        }
        
        else {
        setMessage(data.message || 'An error occurred')
      }
      }
    } catch (error) {
      setMessage('An error occurred')
    } finally {
      setLoading(false)
    }
  }

  // Render VerifyCodePage if the user needs to verify their account
if (showVerification) {
  return (
    <VerifyCodePage
      email={email}
      onVerified={handleVerified}
    />
    
  )
}

  if (showHomePage) {
   navigate('home')
  }

  return (
   
    <div className={`auth-container ${isSignUp ? 'active' : ''}`} id="container">
      <div className="form-container sign-up">
        <form onSubmit={handleSubmit}>
          <h1>Create Account</h1>
          <input
            type="email"
            placeholder="Email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
          <button type="submit" disabled={loading}>
            {loading ? <span className="spinner"></span> : 'Sign Up'}
          </button>
          {message && <p>{message}</p>}
        </form>
      </div>

      <div className="form-container sign-in">
        <form onSubmit={handleSubmit}>
          <h1>Sign In</h1>
          <input
            type="email"
            placeholder="Email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
          <a href="/forgotPassword">Forgot Your Password?</a>
          <button type="submit" disabled={loading}>
            {loading ? <span className="spinner"></span> : 'Sign In'}
          </button>
          {message && <p>{message}</p>}
        </form>
      </div>

      <div className="toggle-container">
        <div className="toggle">
          <div className="toggle-panel toggle-left">
            <h1>Welcome Back!</h1>
            <p>Enter your personal details to use all site features</p>
            <button className="hidden" onClick={handleToggle}>
              Sign In
            </button>
          </div>
          <div className="toggle-panel toggle-right">
            <h1>Hello, Friend!</h1>
            <p>Register with your personal details to use all site features</p>
            <button className="hidden" onClick={handleToggle}>
              Sign Up
            </button>
          </div>
        </div>
      </div>
      {/* Modal for error handling */}
      {showErrorModal && (
        <div className='modal'>
          <div className='modal-content'>
            <div className='my-lottie-player-container'>
              <DotLottieReact src={errorAnimation} loop autoplay/>
            </div>
            <h2>Error</h2>
            <p>{errorMessage}</p>
            <button onClick={() => setShowErrorModal(false)}>Close</button>
          </div>
        </div>
      )}
    </div>
  );
}

export default AuthPage;