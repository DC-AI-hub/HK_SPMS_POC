import React, { createContext, useContext, useCallback } from 'react';

const ErrorContext = createContext();

export const ErrorProvider = ({ children }) => {
  // 全局 Snackbar 已移除，保留 API 为空实现，防止调用方报错
  const addError = useCallback(() => {}, []);
  const removeError = useCallback(() => {}, []);
  return (
    <ErrorContext.Provider value={{ addError, removeError }}>
      {children}
    </ErrorContext.Provider>
  );
};

export const useError = () => {
  const context = useContext(ErrorContext);
  if (!context) {
    throw new Error('useError must be used within an ErrorProvider');
  }
  return context;
};
