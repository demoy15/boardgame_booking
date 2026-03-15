import React, {useEffect, useMemo, useState} from 'react'
import Games from './components/Games'
import Cart from './components/Cart'
import RegisterModal from './components/RegisterModal'
import LoginModal from './components/LoginModal'
import ProfileDrawer from './components/ProfileDrawer'
import {getUser} from './api'
import {useCart} from './components/CartContext'

export default function App() {
    const {items, clear} = useCart()
    const [cartOpen, setCartOpen] = useState(false)
    const [user, setUser] = useState(() => {
        try {
            return JSON.parse(localStorage.getItem('user') || 'null')
        } catch {
            return null
        }
    })
    const [registerOpen, setRegisterOpen] = useState(false)
    const [loginOpen, setLoginOpen] = useState(false)
    const [profileOpen, setProfileOpen] = useState(false)
    const [userError, setUserError] = useState(null)
    const [theme, setTheme] = useState(() => localStorage.getItem('theme') || 'light')

    useEffect(() => {
        if (!user) return
        getUser(user.id)
            .then((fresh) => {
                localStorage.setItem('user', JSON.stringify(fresh))
                setUser(fresh)
            })
            .catch(() => {
                setUserError('User not found. Please register or sign in again.')
                setUser(null)
            })
    }, [])

    useEffect(() => {
        document.documentElement.dataset.theme = theme
        localStorage.setItem('theme', theme)
    }, [theme])

    const cartCount = useMemo(() => items.length, [items.length])

    const onAuth = (u) => {
        localStorage.setItem('user', JSON.stringify(u))
        setUser(u)
        setRegisterOpen(false)
        setLoginOpen(false)
        setUserError(null)
    }

    const signOut = () => {
        localStorage.removeItem('user')
        setUser(null)
        setProfileOpen(false)
        clear()
    }

    const toggleTheme = () => setTheme(theme === 'light' ? 'dark' : 'light')

    return (
        <div className="app">
            <header className="topbar">
                <div className="container">
                    <div className="brand">
                        <h1 className="logo">BoardBox</h1>
                        <span className="tagline">Board game booking made easy</span>
                    </div>
                    <div className="userBar">
                        <button className="ghost" onClick={toggleTheme}>
                            {theme === 'light' ? 'Dark mode' : 'Light mode'}
                        </button>
                        {user ? (
                            <button className="ghost" onClick={() => setProfileOpen(true)}>
                                {user.name}
                            </button>
                        ) : (
                            <>
                                <button className="ghost" onClick={() => setLoginOpen(true)}>
                                    Sign in
                                </button>
                                <button className="ghost" onClick={() => setRegisterOpen(true)}>
                                    Register
                                </button>
                            </>
                        )}
                    </div>
                </div>
            </header>

            <main className="container">
                {userError && <div className="alert">{userError}</div>}
                <Games user={user} onOpenCart={() => setCartOpen(true)}/>
            </main>

            <button className="cartButton" onClick={() => setCartOpen(true)}>
                <span>Cart</span>
                <span className="badge">{cartCount}</span>
            </button>

            <Cart open={cartOpen} onClose={() => setCartOpen(false)} user={user}/>

            <RegisterModal
                open={registerOpen}
                onClose={() => setRegisterOpen(false)}
                onRegistered={onAuth}
            />

            <LoginModal
                open={loginOpen}
                onClose={() => setLoginOpen(false)}
                onLoggedIn={onAuth}
            />

            <ProfileDrawer
                open={profileOpen}
                onClose={() => setProfileOpen(false)}
                user={user}
                onSignOut={signOut}
            />

            <footer className="footer">
                <div className="container">Demo - Board game rental</div>
            </footer>
        </div>
    )
}
