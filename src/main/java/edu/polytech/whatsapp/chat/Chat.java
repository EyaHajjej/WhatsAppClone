package edu.polytech.whatsapp.chat;

import edu.polytech.whatsapp.common.BaseAuditingEntity;
import edu.polytech.whatsapp.message.Message;
import edu.polytech.whatsapp.message.MessageState;
import edu.polytech.whatsapp.message.MessageType;
import edu.polytech.whatsapp.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

import static jakarta.persistence.GenerationType.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name= "chat")
@NamedQuery(name = ChatConstants.FIND_CHAT_BY_SENDER_ID,
            query = "SELECT DISTINCT c FROM Chat c WHERE c.sender.id = :senderId OR c.recipient.id = :senderId ORDER BY createdDate DESC")
@NamedQuery(name = ChatConstants.FIND_CHAT_BY_SENDER_ID_AND_RECEIVER,
            query = "SELECT DISTINCT c FROM Chat c WHERE (c.sender.id = :senderId AND c.recipient.id = :recipientId) OR  (c.sender.id = :recipientId AND c.recipient.id = :senderId)")
public class Chat extends BaseAuditingEntity {
  @Id
  @GeneratedValue(strategy = UUID)
  private String id;

  @ManyToOne
  @JoinColumn(name = "sender_id")
  private User sender;
  @ManyToOne
  @JoinColumn(name = "recipient_id")
  private User recipient;
  @OneToMany(mappedBy = "chat", fetch = FetchType.EAGER)
  @OrderBy("createdDate DESC")
  private List<Message> messages;

  @Transient
    public String getChatName(final String senderId){
      if (recipient.getId().equals(senderId)) {
          return  sender.getFirstName() + " " +  sender.getLastName();

      }
      return recipient.getFirstName() + " " + recipient.getLastName();
  }

    @Transient
    public String getTargetChatName(final String senderId){
        if (sender.getId().equals(senderId)) {
            return  sender.getFirstName() + " " +  sender.getLastName();

        }
        return recipient.getFirstName() + " " + recipient.getLastName();
    }

  @Transient
    public long getUnreadMessages(final String senderId){
      return messages
              .stream()
              .filter(m -> m.getReceiverId().equals(senderId))
              .filter(m -> MessageState.SENT == m.getState())
              .count();

}
  @Transient
    public String getLastMessage(){
      if (messages != null && !messages.isEmpty()) {
          if (messages.get(0).getType() != MessageType.TEXT) {
              return "Attachement";
          }
          return messages.get(0).getContent();
      }
return null;
  }
     @Transient
     public LocalDateTime getLastMessageTime(){
         if (messages != null && !messages.isEmpty()) {
           return messages.get(0).getCreatedDate();
         }
    return null;
     }
}
