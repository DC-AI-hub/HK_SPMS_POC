import React, { Component } from 'react';
import { useTranslation } from 'react-i18next';

class ErrorBoundary extends Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, error: null, errorInfo: null };
  }

  static getDerivedStateFromError(error) {
    return { hasError: true, error };
  }

  componentDidCatch(error, errorInfo) {
    this.setState({ errorInfo });
    console.error('ErrorBoundary caught an error:', error, errorInfo);
  }

  render() {
    if (this.state.hasError) {
      return (
        <div style={{ padding: '20px', border: '1px solid #f44336', borderRadius: '4px', background: '#ffebee' }}>
          <h3 style={{ color: '#f44336' }}>{this.props.title || 'Editor Error'}</h3>
          <p>{this.state.error && this.state.error.toString()}</p>
          <details style={{ marginTop: '10px' }}>
            <summary>Error details</summary>
            <pre style={{ whiteSpace: 'pre-wrap' }}>
              {this.state.errorInfo && this.state.errorInfo.componentStack}
            </pre>
          </details>
        </div>
      );
    }

    return this.props.children;
  }
}

// Hook to use ErrorBoundary with translations
export const useErrorBoundary = (title) => {
  const { t } = useTranslation();
  return (children) => <ErrorBoundary title={t(title)}>{children}</ErrorBoundary>;
};

export default ErrorBoundary;
