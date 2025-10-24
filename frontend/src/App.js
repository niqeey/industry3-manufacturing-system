import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { Container, Row, Col } from 'react-bootstrap';
import Sidebar from './components/Sidebar';
import Dashboard from './pages/Dashboard';
import Products from './pages/Products';
import WorkOrders from './pages/WorkOrders';
import Inventory from './pages/Inventory';
import './App.css';

function App() {
  return (
    <Router>
      <div className="App">
        <Container fluid>
          <Row>
            <Col md={2} className="p-0">
              <Sidebar />
            </Col>
            <Col md={10} className="main-content">
              <Routes>
                <Route path="/" element={<Dashboard />} />
                <Route path="/dashboard" element={<Dashboard />} />
                <Route path="/products" element={<Products />} />
                <Route path="/work-orders" element={<WorkOrders />} />
                <Route path="/inventory" element={<Inventory />} />
              </Routes>
            </Col>
          </Row>
        </Container>
      </div>
    </Router>
  );
}

export default App;
