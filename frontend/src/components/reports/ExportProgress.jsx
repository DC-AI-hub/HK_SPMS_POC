import React, { useState, useEffect } from 'react';
import { Dialog, DialogTitle, DialogContent, LinearProgress, Typography, Box } from '@mui/material';

export default function ExportProgress({ open, onClose, reportType }) {
  const [progress, setProgress] = useState(0);

  useEffect(() => {
    if (open) {
      setProgress(0);
      const timer = setInterval(() => {
        setProgress((prev) => {
          if (prev >= 100) {
            clearInterval(timer);
            setTimeout(onClose, 600);
            return 100;
          }
          return prev + 15;
        });
      }, 200);
      return () => clearInterval(timer);
    }
  }, [open, onClose]);

  return (
    <Dialog open={open} maxWidth="sm" fullWidth>
      <DialogTitle>导出报告</DialogTitle>
      <DialogContent>
        <Box sx={{ mt: 2 }}>
          <Typography variant="body2" color="text.secondary" gutterBottom>
            正在导出 {reportType} 报告...
          </Typography>
          <LinearProgress variant="determinate" value={progress} sx={{ mt: 1 }} />
          <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
            {progress}% 完成
          </Typography>
        </Box>
      </DialogContent>
    </Dialog>
  );
}


