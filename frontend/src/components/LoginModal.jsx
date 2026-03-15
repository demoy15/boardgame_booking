import React, {useState} from 'react'
import {getUserByEmail} from '../api'

export default function LoginModal({open, onClose, onLoggedIn}) {
    const [email, setEmail] = useState('')
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState(null)

    if (!open) return null

    const submit = async (e) => {
        e.preventDefault()
        setError(null)
        setLoading(true)
        try {
            const user = await getUserByEmail(email)
            onLoggedIn(user)
        } catch (err) {
            setError(err.message)
        } finally {
            setLoading(false)
        }
    }

    return (
        <div className="modal">
            <div className="modalCard">
                <h3>Sign in</h3>
                <p className="muted">Use the email you registered with.</p>
                {error && <div className="alert">{error}</div>}
                <form onSubmit={submit}>
                    <label>Email
                        <input value={email} onChange={(e) => setEmail(e.target.value)}
                               placeholder="you@example.com"/>
                    </label>
                    <div className="row">
                        <button type="submit" disabled={loading || !email.trim()}>
                            Sign in
                        </button>
                        <button type="button" className="ghost" onClick={onClose}>
                            Close
                        </button>
                    </div>
                </form>
            </div>
        </div>
    )
}
