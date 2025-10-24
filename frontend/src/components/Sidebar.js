import React from 'react';
import { Nav, Navbar } from 'react-bootstrap';
import { LinkContainer } from 'react-router-bootstrap';

const Sidebar = () => {
  return (
    <div className="sidebar">
      <Navbar bg="dark" variant="dark" className="flex-column align-items-start p-3">
        <Navbar.Brand href="#" className="mb-3">
          <strong>Manufacturing 3.0</strong>
        </Navbar.Brand>
        <Nav className="flex-column w-100">
          <LinkContainer to="/dashboard">
            <Nav.Link className="mb-2">
              <i className="fas fa-tachometer-alt me-2"></i>
              Dashboard
            </Nav.Link>
          </LinkContainer>
          <LinkContainer to="/products">
            <Nav.Link className="mb-2">
              <i className="fas fa-box me-2"></i>
              Products
            </Nav.Link>
          </LinkContainer>
          <LinkContainer to="/work-orders">
            <Nav.Link className="mb-2">
              <i className="fas fa-tasks me-2"></i>
              Work Orders
            </Nav.Link>
          </LinkContainer>
          <LinkContainer to="/inventory">
            <Nav.Link className="mb-2">
              <i className="fas fa-warehouse me-2"></i>
              Inventory
            </Nav.Link>
          </LinkContainer>
        </Nav>
      </Navbar>
    </div>
  );
};

export default Sidebar;
