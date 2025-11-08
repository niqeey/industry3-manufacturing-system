import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Table, Button, Alert } from 'react-bootstrap';
import { workOrderAPI } from '../services/api';

const Dashboard = () => {
  const [metrics, setMetrics] = useState({
    totalWorkOrders: 0,
    inProgressWorkOrders: 0,
    completedWorkOrders: 0,
    plannedWorkOrders: 0
  });
  const [recentWorkOrders, setRecentWorkOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    try {
      setLoading(true);
      setError(null);
      
      // Load all work orders first
      const allWorkOrdersResponse = await workOrderAPI.getAll();
      const allWorkOrders = allWorkOrdersResponse.data;
      
      // Calculate metrics from all work orders
      const totalWorkOrders = allWorkOrders.length;
      const inProgressWorkOrders = allWorkOrders.filter(wo => wo.status === 'IN_PROGRESS').length;
      const completedWorkOrders = allWorkOrders.filter(wo => wo.status === 'COMPLETED').length;
      const plannedWorkOrders = allWorkOrders.filter(wo => wo.status === 'PLANNED').length;

      setMetrics({
        totalWorkOrders,
        inProgressWorkOrders,
        completedWorkOrders,
        plannedWorkOrders
      });

      // Get recent work orders (last 10)
      setRecentWorkOrders(allWorkOrders.slice(0, 10));

    } catch (err) {
      console.error('Error loading dashboard data:', err);
      console.error('Error details:', err.response?.data || err.message);
      setError(`Failed to load dashboard data: ${err.response?.data?.message || err.message}`);
    } finally {
      setLoading(false);
    }
  };

  const getStatusBadge = (status) => {
    // Handle undefined/null status
    if (!status) {
      return (
        <span className="badge bg-secondary">
          UNKNOWN
        </span>
      );
    }
    
    const statusClasses = {
      'PLANNED': 'bg-secondary',
      'IN_PROGRESS': 'bg-primary',
      'COMPLETED': 'bg-success',
      'CANCELLED': 'bg-danger',
      'ON_HOLD': 'bg-warning'
    };
    
    return (
      <span className={`badge ${statusClasses[status] || 'bg-secondary'}`}>
        {status.replace('_', ' ')}
      </span>
    );
  };

  const getPriorityClass = (priority) => {
    const priorityClasses = {
      'HIGH': 'priority-high',
      'URGENT': 'priority-urgent',
      'NORMAL': 'priority-normal',
      'LOW': 'priority-low'
    };
    
    return priorityClasses[priority] || 'priority-normal';
  };

  if (loading) {
    return (
      <Container className="mt-4">
        <div className="text-center">
          <div className="spinner-border" role="status">
            <span className="visually-hidden">Loading...</span>
          </div>
        </div>
      </Container>
    );
  }

  return (
    <Container fluid>
      <Row className="mb-4">
        <Col>
          <h1>Manufacturing Dashboard</h1>
          <p className="text-muted">Overview of your manufacturing operations</p>
        </Col>
      </Row>

      {error && (
        <Row className="mb-4">
          <Col>
            <Alert variant="danger">
              {error}
              <Button variant="link" onClick={loadDashboardData} className="ms-2">
                Retry
              </Button>
            </Alert>
          </Col>
        </Row>
      )}

      {/* Metrics Cards */}
      <Row className="mb-4">
        <Col md={3}>
          <Card className="dashboard-metric bg-primary text-white">
            <Card.Body>
              <h3>{metrics.totalWorkOrders}</h3>
              <p>Total Work Orders</p>
            </Card.Body>
          </Card>
        </Col>
        <Col md={3}>
          <Card className="dashboard-metric bg-info text-white">
            <Card.Body>
              <h3>{metrics.inProgressWorkOrders}</h3>
              <p>In Progress</p>
            </Card.Body>
          </Card>
        </Col>
        <Col md={3}>
          <Card className="dashboard-metric bg-success text-white">
            <Card.Body>
              <h3>{metrics.completedWorkOrders}</h3>
              <p>Completed</p>
            </Card.Body>
          </Card>
        </Col>
        <Col md={3}>
          <Card className="dashboard-metric bg-secondary text-white">
            <Card.Body>
              <h3>{metrics.plannedWorkOrders}</h3>
              <p>Planned</p>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Recent Work Orders */}
      <Row>
        <Col>
          <Card>
            <Card.Header>
              <h5>Recent Work Orders</h5>
            </Card.Header>
            <Card.Body>
              {recentWorkOrders.length === 0 ? (
                <p className="text-muted">No work orders found</p>
              ) : (
                <div className="table-responsive">
                  <Table striped hover>
                    <thead>
                      <tr>
                        <th>Work Order #</th>
                        <th>Product</th>
                        <th>Quantity</th>
                        <th>Status</th>
                        <th>Priority</th>
                        <th>Planned Start</th>
                      </tr>
                    </thead>
                    <tbody>
                      {recentWorkOrders.map((workOrder) => (
                        <tr key={workOrder.id}>
                          <td>
                            <strong>{workOrder.workOrderNumber}</strong>
                          </td>
                          <td>{workOrder.product?.name || 'N/A'}</td>
                          <td>
                            {workOrder.quantityCompleted}/{workOrder.quantityOrdered}
                          </td>
                          <td>{getStatusBadge(workOrder.status)}</td>
                          <td>
                            <span className={getPriorityClass(workOrder.priority)}>
                              {workOrder.priority}
                            </span>
                          </td>
                          <td>
                            {workOrder.plannedStartDate 
                              ? new Date(workOrder.plannedStartDate).toLocaleDateString()
                              : 'Not scheduled'
                            }
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </Table>
                </div>
              )}
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default Dashboard;
