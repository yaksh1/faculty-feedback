import React, { useState } from 'react'
import '../signup_login/AuthPage.css'
import '../utils/modal.css'
import { DotLottieReact } from '@lottiefiles/dotlottie-react'
import tickAnimation from '../../assets/animations/tick.lottie'
import { useNavigate } from 'react-router-dom' // Import useNavigate
import errorAnimation from '../../assets/animations/error.lottie'
import passwordAnimation from '../../assets/animations/password.lottie'

import AuthPage from '../signup_login/AuthPage'

function ForgotPasswordPage () {
  const [email, setEmail] = useState('')
  const [password, setNewPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [message, setMessage] = useState('')
  const [loading, setLoading] = useState(false)
  const [showModal, setShowModal] = useState(false)
  const [showErrorModal, setShowErrorModal] = useState(false)
  const [errorMessage, setErrorMessage] = useState('')
  const navigate = useNavigate() // Initialize navigate

  const handleSubmit = async e => {
    e.preventDefault()
    setLoading(true)

    if (password !== confirmPassword) {
      setErrorMessage('Passwords do not match.')
      setShowErrorModal(true)
      setLoading(false)
      return
    }

    try {
      const response = await fetch(
        'http://localhost:8080/auth/forgotPassword',
        {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({ email, password })
        }
      )

      const data = await response.json()

      if (response.ok) {
  
  setShowModal(true)
} else {
  if (data.errorCode === 'INVALID_PASSWORD') {
    setErrorMessage('Password is weak,please use a strong password.') // Set the error message
    setShowErrorModal(true) // Show the error modal
  } else if (data.errorCode === 'INVALID_EMAIL') {
    setErrorMessage('Email is not registered with MITWPU.') // Set the error message
    setShowErrorModal(true) // Show the error modal
  } else if (data.errorCode === 'USER_NOT_FOUND') {
    setErrorMessage('User does not exists with this email id.') // Set the error message
    setShowErrorModal(true) // Show the error modal
  } else if (data.errorCode === 'ACCOUNT_NOT_VERIFIED') {
    setErrorMessage('Account not verified,please verify your account.') // Set the error message
    setShowErrorModal(true) // Show the error modal
  } else {
    setErrorMessage(data.message || 'An error occurred. Please try again.')
    setShowErrorModal(true)
  }
}
    } catch (error) {
      setErrorMessage('An error occurred. Please try again.')
      setShowErrorModal(true)
    } finally {
      setLoading(false)
    }
  }
  const handleBackToLogin = () => { 
    setShowModal(false)
    navigate("/")
  }

  return (
    <div className='container'>
      <div className='form-container'>
        <div className='my-password-lottie-player-container'>
  <DotLottieReact src={passwordAnimation} loop autoplay />
</div>

        <form onSubmit={handleSubmit}>
          <h1>Reset Password</h1>
          <input
            type='email'
            placeholder='Enter your email'
            value={email}
            onChange={e => setEmail(e.target.value)}
            required
          />
          <input
            type='password'
            placeholder='New Password'
            value={password}
            onChange={e => setNewPassword(e.target.value)}
            required
          />
          <input
            type='password'
            placeholder='Confirm New Password'
            value={confirmPassword}
            onChange={e => setConfirmPassword(e.target.value)}
            required
          />
          <button type='submit' disabled={loading}>
            {loading ? <span className='spinner'></span> : 'Reset Password'}
          </button>
          {message && <p>{message}</p>}
        </form>
      </div>

      {showModal && (
        <div className='modal'>
          <div className='modal-content'>
            <div className='my-lottie-player-container'>
              <DotLottieReact src={tickAnimation} loop autoplay />
            </div>
            <h4>Password Reset Successfully!</h4>
            <button onClick={handleBackToLogin}>Back to Login</button>
          </div>
        </div>
      )}

      {showErrorModal && (
        <div className='modal'>
          <div className='modal-content'>
            <div className='my-lottie-player-container'>
              <DotLottieReact src={errorAnimation} loop autoplay />
            </div>
            <h2>Error</h2>
            <p>{errorMessage}</p>
            <button onClick={() => setShowErrorModal(false)}>Close</button>
          </div>
        </div>
      )}
    </div>
  )
}

export default ForgotPasswordPage
