package org.example.project.ui.screens

import Dialogwind
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.example.project.ui.them.TrajectoryColors
import org.example.project.ui.them.TrajectoryTyp

@Composable
fun infoDialog(onDismiss: () -> Unit) {

   Dialogwind (transpearence = 0.25f ,
       450.dp, 300.dp , onDismiss = onDismiss , modifier = Modifier
   ){
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            IconButton(onClick = { onDismiss() }) {
                                Icon(
                                    modifier = Modifier.size(22.dp),
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = TrajectoryColors.TextMuted
                                )
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = "Hello!",
                            style = MaterialTheme.typography.headlineMedium,
                            color = TrajectoryColors.Background,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = "Thank you for using Trajectory.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TrajectoryColors.Background,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(Modifier.height(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "We hope it helps you model, simulate, and predict with confidence. Wishing you a smooth and productive experience.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TrajectoryColors.TextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }

                        Spacer(Modifier.height(24.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "With care, the ",
                                color = TrajectoryColors.Background,
                                fontSize = 13.sp,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                "WELLEDG",
                                color = TrajectoryColors.LimeGreen,
                                fontFamily = TrajectoryTyp.welledge,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                " team",
                                color = TrajectoryColors.Background,
                                fontSize = 13.sp,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
