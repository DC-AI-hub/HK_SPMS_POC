import { Card, CardMedia, CardContent, CardActions, Typography, Skeleton, Box, useTheme } from '@mui/material'
import { useTranslation } from 'react-i18next'


function formatDate(input) {


  if (!input) return null
  try {
    return new Date(input).toLocaleString()
  } catch {
    return String(input)
  }
}

const StatCard = ({ title, description, value, asOfDate, isLoading = false }) => {
  const { t }= useTranslation('common');
  const formattedDate = formatDate(asOfDate)
  const theme = useTheme();
  if (isLoading) {
    return (
      <Card sx={{ boxShadow: 1, borderRadius: 2, 
          width: '100%', 
          minWidth: { xs: 200, sm: 240, md: 280 }, display: 'flex', 
          flexDirection: 'column' }}>

        <Skeleton variant="rectangular" height={200} width="100%" />
        <CardContent sx={{ flex: '0 0 214px' }}>
          <Skeleton width="60%" />
          <Skeleton width="40%" />
          <Skeleton width="30%" height={36} sx={{ mt: 1 }} />
        </CardContent>
        <CardActions sx={{ justifyContent: 'flex-end', py: 1.5, flex: '0 0 48px' }}>
          <Skeleton width={120} />
        </CardActions>
      </Card>
    )
  }

  return (
    <Card
      sx={{
        minWidth: { xs: 360, sm: 320, md: 300 },
        width: '100%',  
        boxShadow: 1,
        borderRadius: 2,
        display: 'flex',
        flexDirection: 'column',
        transition: 'box-shadow 150ms ease, transform 150ms ease',
        '&:hover': { boxShadow: 8, transform: 'translateY(-2px)' }
      }}
    >
      <CardContent sx={{ flex: '0 0 214px' }}>
        <Typography variant="h3.heading" color="text.secondary">
          {title}
        </Typography>
        {description && (
          <Typography variant="body2" height={80} color="text.secondary" sx={{ mt: 1 }}>
            {description}
          </Typography>
        )}
        <Typography variant="h4" sx={{ mt: 1,textAlign:'center',mr: 2 }} color={theme.palette.primary.main}>
          {value !== null && value !== undefined ? value : 'N/A'}
        </Typography>
      </CardContent>
      <CardActions sx={{ justifyContent: 'flex-end', py: 1.5, flex: '0 0 48px' }}>
        {formattedDate && (
          <Box sx={{ px: 1 }}>
            <Typography variant="caption" color="text.secondary">
               {t("as-of") + formattedDate}
            </Typography>
          </Box>
        )}
      </CardActions>
    </Card>
  )
}

export default StatCard


//<CardMedia sx={{ height: 200, bgcolor: '#EFEFF0', opacity: 0.9 }} title={title} />
