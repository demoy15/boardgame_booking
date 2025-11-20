import React from 'react'
import {Link, Route, Routes} from 'react-router-dom'
import Games from './components/Games'
import Orders from './components/Orders'

export default function App() {
    return (
        <div className="app">
            <header className="topbar">
                <div className="container">
                    <h1 className="logo">BoardBox</h1>
                    <nav>
                        <Link to="/">Games</Link>
                        <Link to="/orders">Orders</Link>
                    </nav>
                </div>
            </header>

            <main className="container">
                <Routes>
                    <Route path="/" element={<Games/>}/>
                    <Route path="/orders" element={<Orders/>}/>
                </Routes>
            </main>

            <footer className="footer">
                <div className="container">Demo — Board game rental</div>
            </footer>
        </div>
    )
}
